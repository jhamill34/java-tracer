package tech.jhamill34.analyze;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;

public interface HeapStore {
    void saveMethod(int id, MethodNode methodNode);

    void saveInstruction(int id, AbstractInsnNode insnNode);


    MethodNode getMethod(int id);

    void record(AbstractInsnNode insnNode, IdValue produced, List<? extends IdValue> consumed);


    void addToHeap(IdValue ref, String name, IdValue value);
    void addToHeap(String klass, String name, IdValue value);

    IdValue getFromHeap(IdValue ref, String name, IdValue defaultValue);
    IdValue getFromHeap(String klass, String name, IdValue defaultValue);


    IdValue findById(int id);

    Collection<Integer> findConsumedByInstruction(int insnNode);
    Collection<Integer> findProducedByInstruction(int insnNode);
    Collection<Integer> findInstructionsConsumingValue(int value);
    Collection<Integer> findInstructionProducingValue(int value);

    Collection<Integer> allValues();

    void createArray(IdValue ref, IdValue proxyValue);
    void storeArray(IdValue ref, IdValue value);
    IdValue loadArray(IdValue ref);
    Collection<Integer> expandArrayValue(int proxyValueId);
    Integer findProxyValue(int valueId);
}
