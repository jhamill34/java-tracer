package tech.jhamill34.analyze.handlers;

import com.google.inject.Inject;
import org.objectweb.asm.tree.FieldInsnNode;
import tech.jhamill34.analyze.FieldHandler;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.analyze.HeapStore;

public class FieldHandlerImpl implements FieldHandler {
    @Inject
    private HeapStore heapStore;

    @Override
    public IdValue handleGetStatic(FieldInsnNode insn, IdValue defaultValue) {
        return heapStore.getFromHeap(insn.owner, insn.name, defaultValue);
    }

    @Override
    public IdValue handleGetField(FieldInsnNode insn, IdValue ref, IdValue defaultValue) {
        return heapStore.getFromHeap(ref, insn.name, defaultValue);
    }

    @Override
    public void handlePutStatic(FieldInsnNode insn, IdValue value) {
        heapStore.addToHeap(insn.owner, insn.name, value);
    }

    @Override
    public void handlePutField(FieldInsnNode insn, IdValue ref, IdValue value) {
        heapStore.addToHeap(ref, insn.name, value);
    }
}
