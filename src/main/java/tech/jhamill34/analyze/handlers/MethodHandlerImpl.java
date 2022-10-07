package tech.jhamill34.analyze.handlers;

import com.google.inject.Inject;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.analyze.MethodHandler;
import tech.jhamill34.analyze.ValueContainer;
import tech.jhamill34.analyze.AnalyzerFactory;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.analyze.InterpreterFactory;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class MethodHandlerImpl implements MethodHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandlerImpl.class);

    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private HeapStore heapStore;

    @Inject
    private AnalyzerFactory analyzerFactory;

    @Inject
    private InterpreterFactory interpreterFactory;

    private final Map<String, IdValue> cachedExecutions = new HashMap<>();

    @Override
    public IdValue invoke(MethodInsnNode insn, List<? extends IdValue> args, IdValue defaultValue) {
        String owner = insn.owner;

        if (insn.getOpcode() != INVOKESTATIC && insn.getOpcode() != INVOKESPECIAL) {
            BasicValue ref = args.get(0).delegate;
            owner = ref.getType().getInternalName();
        }

        String method = insn.name + insn.desc;
        ValueContainer returnedValue = new ValueContainer();

        if (execute(owner, method, args, returnedValue)) {
            return returnedValue.getValue();
        }

        return defaultValue;
    }

    private boolean execute(String owner, String method, List<? extends IdValue> args, ValueContainer returnValue) {
        int classId = classRepository.getId(owner);
        int methodId = methodRepository.getId(classId, method);

        String cacheKey = lookupKey(classId, methodId, args);
        if (cachedExecutions.containsKey(cacheKey)) {
            returnValue.setValue(cachedExecutions.get(cacheKey));
            return true;
        }

        MethodNode methodNode = heapStore.getMethod(methodId);

        if (methodNode != null) {
            logger.info("Invoking " + owner + "." + method);
            Interpreter<IdValue> interpreter = interpreterFactory.createRecursiveInterpreter(args, returnValue);
            Analyzer<IdValue> analyzer = analyzerFactory.createAnalyzer(interpreter);

            try {
                analyzer.analyze(owner, methodNode);

                if (returnValue.getValue() != null) {
                    cachedExecutions.put(cacheKey, returnValue.getValue());
                    return true;
                }

                return false;
            } catch (AnalyzerException e) {
                logger.error("Something went wrong analyzing " + owner + " " + method, e);
            }
        } else {
            logger.warn("No implementation found for " + owner + " " + method);
        }

        return false;
    }

    private String lookupKey(int classId, int methodId, List<? extends IdValue> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("C").append(classId).append("M").append(methodId);

        for (IdValue arg : args) {
            sb.append("V").append(arg.id);
        }

        return sb.toString();
    }
}
