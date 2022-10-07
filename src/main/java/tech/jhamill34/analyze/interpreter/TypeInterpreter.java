package tech.jhamill34.analyze.interpreter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;

public class TypeInterpreter extends BasicInterpreter {
    public final static BasicValue NULL = new BasicValue(null);
    public final static BasicValue MAYBENULL = new BasicValue(null);

    public TypeInterpreter() {
        super(ASM9);
    }

    @Override
    public BasicValue newValue(Type type) {
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE;
        }

        boolean isArray = type.getSort() == Type.ARRAY;
        if (isArray) {
            switch (type.getElementType().getSort()) {
                case Type.BOOLEAN:
                case Type.CHAR:
                case Type.BYTE:
                case Type.SHORT:
                    return new BasicValue(type);
                default:
                    break;
            }
        }

        BasicValue value = super.newValue(type);
        if (BasicValue.REFERENCE_VALUE.equals(value)) {
            if (isArray) {
                value = newValue(type.getElementType());
                StringBuilder descriptor = new StringBuilder();
                for (int i = 0; i < type.getDimensions(); ++i) {
                    descriptor.append('[');
                }
                descriptor.append(value.getType().getDescriptor());
                value = new BasicValue(Type.getType(descriptor.toString()));
            } else {
                value = new BasicValue(type);
            }
        }

        return value;
    }

    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        if (insn.getOpcode() == ACONST_NULL) {
            return NULL;
        }
        return super.newOperation(insn);
    }

    @Override
    public BasicValue merge(BasicValue value1, BasicValue value2) {
        if (isRef(value1) && isRef(value2) && value1 != value2) {
            return MAYBENULL;
        }
        return super.merge(value1, value2);
    }

    private boolean isRef(Value v) {
        return v == BasicValue.REFERENCE_VALUE || v == NULL || v == MAYBENULL;
    }
}
