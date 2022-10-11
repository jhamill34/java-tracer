package tech.jhamill34.analyze.interpreter;

import lombok.Builder;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
import tech.jhamill34.analyze.ArrayHandler;
import tech.jhamill34.analyze.MethodHandler;
import tech.jhamill34.analyze.ValueContainer;
import tech.jhamill34.analyze.FieldHandler;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.analyze.HeapStore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrackingInterpreter extends DelegatingInterpreter {
    private final List<? extends IdValue> parameters;
    private final ValueContainer returnValue;
    private final HeapStore heapStore;
    private final FieldHandler fieldHandler;
    private final MethodHandler methodHandler;

    private final ArrayHandler arrayHandler;

    @Builder(builderMethodName = "recursiveBuilder")
    public TrackingInterpreter(
            Interpreter<BasicValue> delegate,
            List<? extends IdValue> parameters,
            ValueContainer returnValue,
            HeapStore heapStore,
            FieldHandler fieldHandler,
            MethodHandler methodHandler,
            ArrayHandler arrayHandler
    ) {
        super(delegate);
        this.parameters = parameters;
        this.returnValue = returnValue;
        this.heapStore = heapStore;
        this.fieldHandler = fieldHandler;
        this.methodHandler = methodHandler;
        this.arrayHandler = arrayHandler;
    }

    @Override
    public IdValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        IdValue result = super.newOperation(insn);

        if (insn instanceof FieldInsnNode) {
            FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;

            if (fieldInsnNode.getOpcode() == GETSTATIC) {
                result = fieldHandler.handleGetStatic(fieldInsnNode, result);
            }
        }

        heapStore.record(insn, result, Collections.emptyList());
        return result;
    }

    @Override
    public IdValue copyOperation(AbstractInsnNode insn, IdValue value) throws AnalyzerException {
        IdValue result = super.copyOperation(insn, value);

        heapStore.record(insn, result, Collections.singletonList(value));
        return result;
    }

    @Override
    public IdValue unaryOperation(AbstractInsnNode insn, IdValue value) throws AnalyzerException {
        IdValue result = super.unaryOperation(insn, value);

        if (insn instanceof FieldInsnNode) {
            FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;

            switch (fieldInsnNode.getOpcode()) {
                case PUTSTATIC:
                    fieldHandler.handlePutStatic(fieldInsnNode, value);
                    break;
                case GETFIELD:
                    result = fieldHandler.handleGetField(fieldInsnNode, value, result);

            }
        } else if (insn.getOpcode() == ANEWARRAY || insn.getOpcode() == NEWARRAY) {
            // ANEWARRAY, NEWARRAY
            arrayHandler.alloc(result);
        }

        heapStore.record(insn, result, Collections.singletonList(value));
        return result;
    }

    @Override
    public IdValue binaryOperation(AbstractInsnNode insn, IdValue value1, IdValue value2) throws AnalyzerException {
        IdValue result = super.binaryOperation(insn, value1, value2);

        if (insn instanceof FieldInsnNode) {
            FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;
            if (fieldInsnNode.getOpcode() == PUTFIELD) {
                fieldHandler.handlePutField(fieldInsnNode, value1, value2);
            }
        } else if (insn.getOpcode() >= IALOAD && insn.getOpcode() <= SALOAD) {
            // *ALOAD
            result = arrayHandler.load(value1, result);
        }

        List<IdValue> args = Arrays.asList(value1, value2);
        heapStore.record(insn, result, args);
        return result;
    }

    @Override
    public IdValue ternaryOperation(AbstractInsnNode insn, IdValue value1, IdValue value2, IdValue value3) throws AnalyzerException {
        IdValue result = super.ternaryOperation(insn, value1, value2, value3);

        // *ASTORE
        if (insn.getOpcode() >= IASTORE && insn.getOpcode() <= SASTORE) {
            arrayHandler.store(value1, value3);
        }

        List<IdValue> args = Arrays.asList(value1, value2, value3);
        heapStore.record(insn, result, args);
        return result;
    }

    @Override
    public IdValue naryOperation(AbstractInsnNode insn, List<? extends IdValue> values) throws AnalyzerException {
        IdValue result = super.naryOperation(insn, values);

        // MULTIANEWARRAY

        if (insn instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) insn;

            result = methodHandler.invoke(methodInsnNode, values, result);
        }

        heapStore.record(insn, result, values);
        return result;
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, IdValue value, IdValue expected) throws AnalyzerException {
        super.returnOperation(insn, value, expected);

        returnValue.setValue(value);
    }

    @Override
    public IdValue newParameterValue(boolean isInstanceMethod, int local, Type type) {
        if (local < parameters.size()) {
            return parameters.get(local);
        }

        return super.newParameterValue(isInstanceMethod, local, type);
    }
}
