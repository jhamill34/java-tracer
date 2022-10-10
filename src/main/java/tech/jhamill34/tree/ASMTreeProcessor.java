package tech.jhamill34.tree;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.inject.Inject;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.analyze.AnalyzerFactory;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.analyze.InterpreterFactory;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.file.BytecodeTreeProcessor;

public class ASMTreeProcessor implements BytecodeTreeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ASMTreeProcessor.class);

    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private InstructionRepository instructionRepository;

    @Inject
    private FieldRepository fieldRepository;

    @Inject
    private HeapStore heapStore;

    @Inject
    private AnalyzerFactory analyzerFactory;

    @Inject
    private InterpreterFactory interpreterFactory;

    @Override
    public void process(String name, String parent, ClassNode node) {
        int classId = classRepository.save(transformClass(node, parent));
        classRepository.recordSuperClass(classId, node.superName);
        classRepository.recordInterfaces(classId, node.interfaces);

        for (FieldNode fieldNode : node.fields) {
            fieldRepository.save(transformField(fieldNode, classId));
        }

        for (MethodNode methodNode : node.methods) {
            int methodId = methodRepository.save(transformMethod(methodNode, classId));

            try {
                MutableGraph<Integer> controlFlow = GraphBuilder.directed().allowsSelfLoops(true).build();
                Interpreter<IdValue> interpreter = interpreterFactory.createShallowInterpreter();
                Analyzer<IdValue> analyzer = analyzerFactory.createAnalyzer(interpreter, controlFlow);
                analyzer.analyze(node.name, methodNode);
                instructionRepository.recordControlFlow(methodId, controlFlow);
            } catch (AnalyzerException e) {
                logger.warn("Unable to analyze methodId " + methodId, e);
            }

            heapStore.saveMethod(methodId, methodNode);

            int currentLineNumber = -1;
            for (int i = 0; i < methodNode.instructions.size(); i++) {
                AbstractInsnNode insnNode = methodNode.instructions.get(i);
                if (insnNode instanceof LineNumberNode) {
                    LineNumberNode lineNumberNode = (LineNumberNode) insnNode;
                    currentLineNumber = lineNumberNode.line;
                }

                int referenceId = -1;
                InstructionEntity.ReferenceType referenceType = null;
                if (insnNode instanceof MethodInsnNode) {
                    referenceType = InstructionEntity.ReferenceType.METHOD;
                    MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;

                    int ownerId = classRepository.getId(methodInsnNode.owner);
                    if (ownerId < 0) {
                        ownerId = classRepository.save(ClassEntity.builder()
                                .name(methodInsnNode.owner)
                                .packageName("<unknown>")
                                .build());
                    }

                    referenceId = methodRepository.getId(ownerId, methodInsnNode.name + methodInsnNode.desc);
                    if (referenceId < 0) {
                        referenceId = methodRepository.save(MethodEntity.builder()
                                        .ownerId(ownerId)
                                        .name(methodInsnNode.name)
                                        .descriptor(methodInsnNode.desc)
                                        .build());
                    }
                } else if (insnNode instanceof FieldInsnNode) {
                    referenceType = InstructionEntity.ReferenceType.FIELD;
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNode;

                    int ownerId = classRepository.getId(fieldInsnNode.owner);
                    if (ownerId < 0) {
                        ownerId = classRepository.save(ClassEntity.builder()
                                .name(fieldInsnNode.owner)
                                .packageName("<unknown>")
                                .build());
                    }

                    referenceId = fieldRepository.getId(ownerId, fieldInsnNode.name + fieldInsnNode.desc);
                    if (referenceId < 0) {
                        referenceId = fieldRepository.save(FieldEntity.builder()
                                .ownerId(ownerId)
                                .name(fieldInsnNode.name)
                                .descriptor(fieldInsnNode.desc)
                                .build());
                    }
                }

                int instructionId = instructionRepository.save(transformInstruction(insnNode, methodId, currentLineNumber, i, referenceId, referenceType));
                heapStore.saveInstruction(instructionId, insnNode);
            }
        }
    }

    private ClassEntity transformClass(ClassNode classNode, String packageName) {
        return ClassEntity.builder()
                .access(classNode.access)
                .name(classNode.name)
                .packageName(packageName)
                .signature(classNode.signature)
                .build();
    }

    private MethodEntity transformMethod(MethodNode methodNode, int ownerId) {
        return MethodEntity.builder()
                .ownerId(ownerId)
                .access(methodNode.access)
                .name(methodNode.name)
                .descriptor(methodNode.desc)
                .signature(methodNode.signature)
                .build();
    }

    private FieldEntity transformField(FieldNode fieldNode, int ownerId) {
        return FieldEntity.builder()
                .ownerId(ownerId)
                .access(fieldNode.access)
                .name(fieldNode.name)
                .descriptor(fieldNode.desc)
                .signature(fieldNode.signature)
                .build();
    }

    private InstructionEntity transformInstruction(AbstractInsnNode insnNode, int invokerId, int lineNumber, int index, int referenceId, InstructionEntity.ReferenceType referenceType) {
        return InstructionEntity.builder()
                .opCode(insnNode.getOpcode())
                .invokerId(invokerId)
                .index(index)
                .lineNumber(lineNumber)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();
    }
}
