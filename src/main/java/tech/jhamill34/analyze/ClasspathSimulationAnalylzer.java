package tech.jhamill34.analyze;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.inject.Inject;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.pico.ClasspathAnalyzer;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.Collections;

public class ClasspathSimulationAnalylzer implements ClasspathAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ClasspathSimulationAnalylzer.class);

    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private AnalyzerFactory analyzerFactory;

    @Inject
    private InterpreterFactory interpreterFactory;

    @Inject
    private HeapStore heapStore;

    @Override
    public boolean analyze(String mainClass, String entryMethod) {
        String key = mainClass + "." + entryMethod;

        int classId = classRepository.getId(mainClass);
        int methodId = methodRepository.getId(classId, entryMethod);
        MethodNode methodNode = heapStore.getMethod(methodId);

        if (methodNode == null) {
            logger.error("Couldn't find " + key + " in method repo");
            return false;
        }

        Interpreter<IdValue> interpreter = interpreterFactory.createRecursiveInterpreter(Collections.emptyList(), new ValueContainer());
        Analyzer<IdValue> analyzer = analyzerFactory.createAnalyzer(interpreter);

        try {
            analyzer.analyze(mainClass, methodNode);
        } catch (AnalyzerException e) {
            logger.error("Something went wrong during analysis", e);
            return false;
        }

        return true;
    }
}
