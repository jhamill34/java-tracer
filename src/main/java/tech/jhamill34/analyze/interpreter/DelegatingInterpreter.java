package tech.jhamill34.analyze.interpreter;

import lombok.Builder;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
import tech.jhamill34.analyze.IdValue;

import java.util.List;
import java.util.stream.Collectors;

public class DelegatingInterpreter extends Interpreter<IdValue> implements Opcodes {
    private final Interpreter<BasicValue> delegate;

    @Builder(builderMethodName = "shallowBuilder")
    public DelegatingInterpreter(Interpreter<BasicValue> delegate) {
        super(ASM9);
        this.delegate = delegate;
    }

    @Override
    public IdValue newValue(Type type) {
        return wrap(delegate.newValue(type));
    }

    @Override
    public IdValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        return wrap(delegate.newOperation(insn));
    }

    @Override
    public IdValue copyOperation(AbstractInsnNode insn, IdValue value) throws AnalyzerException {
        return wrap(value.id, delegate.copyOperation(insn, value.delegate));
    }

    @Override
    public IdValue unaryOperation(AbstractInsnNode insn, IdValue value) throws AnalyzerException {
        return wrap(delegate.unaryOperation(insn, value.delegate));
    }

    @Override
    public IdValue binaryOperation(AbstractInsnNode insn, IdValue value1, IdValue value2) throws AnalyzerException {
        return wrap(delegate.binaryOperation(insn, value1.delegate, value2.delegate));
    }

    @Override
    public IdValue ternaryOperation(AbstractInsnNode insn, IdValue value1, IdValue value2, IdValue value3) throws AnalyzerException {
        return wrap(delegate.ternaryOperation(insn, value1.delegate, value2.delegate, value3.delegate));
    }

    @Override
    public IdValue naryOperation(AbstractInsnNode insn, List<? extends IdValue> values) throws AnalyzerException {
        return wrap(delegate.naryOperation(insn, values.stream().map(v -> v.delegate).collect(Collectors.toList())));
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, IdValue value, IdValue expected) throws AnalyzerException {
        delegate.returnOperation(insn, value.delegate, expected.delegate);
    }

    @Override
    public IdValue merge(IdValue value1, IdValue value2) {
        BasicValue value = delegate.merge(value1.delegate, value2.delegate);

        if (value.equals(value1.delegate)) {
            return value1;
        }

        return wrap(value);
    }

    private IdValue wrap(BasicValue value) {
        if (value == null) {
            return null;
        }

        return IdValue.from(value);
    }

    private IdValue wrap(int id, BasicValue value) {
        if (value == null) {
            return null;
        }

        return IdValue.from(id, value);
    }
}
