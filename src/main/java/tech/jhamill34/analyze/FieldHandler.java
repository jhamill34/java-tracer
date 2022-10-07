package tech.jhamill34.analyze;

import org.objectweb.asm.tree.FieldInsnNode;

public interface FieldHandler {
    IdValue handleGetStatic(FieldInsnNode insn, IdValue defaultValue);

    IdValue handleGetField(FieldInsnNode insn, IdValue ref, IdValue defaultValue);

    void handlePutStatic(FieldInsnNode insn, IdValue value);

    void handlePutField(FieldInsnNode insn, IdValue ref, IdValue value);
}
