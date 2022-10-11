package tech.jhamill34.heap;

import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.analyze.HeapStore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class InMemoryHeapStore implements HeapStore {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryHeapStore.class);

    private final Map<IdValue, Map<String, Stack<IdValue>>> values = new HashMap<>();
    private final Map<String, Map<String, Stack<IdValue>>> staticValues = new HashMap<>();

    private final Map<IdValue, IdValue> arrayProxy = new HashMap<>();
    private final Map<Integer, Set<Integer>> possibleArrayValues = new HashMap<>();
    private final Map<Integer, Integer> proxiedBy = new HashMap<>();

    private final Map<Integer, IdValue> data = new HashMap<>();
    private final Map<Integer, Integer> producedBy = new HashMap<>();
    private final Multimap<Integer, Integer> produces = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<Integer, Integer> consumedBy = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<Integer, Integer> consuming = MultimapBuilder.hashKeys().hashSetValues().build();

    private final Map<Integer, MethodNode> methodMap = new HashMap<>();
    private final Map<AbstractInsnNode, Integer> insnMap = new HashMap<>();

    @Override
    public void saveMethod(int id, MethodNode methodNode) {
        methodMap.put(id, methodNode);
    }

    @Override
    public MethodNode getMethod(int id) {
        return methodMap.get(id);
    }

    @Override
    public void saveInstruction(int id, AbstractInsnNode insnNode) {
        insnMap.put(insnNode, id);
    }

    @Override
    public void record(AbstractInsnNode insnNode, IdValue produced, List<? extends IdValue> consumed) {
        if (consumed.size() == 1 && consumed.get(0).equals(produced)) {
            return;
        }

        int instructionId = insnMap.get(insnNode);

        if (produced != null) {
            data.putIfAbsent(produced.id, produced);

            producedBy.put(produced.id, instructionId);
            produces.put(instructionId, produced.id);
        } else {
            logger.warn("Produced is null " + insnNode.getOpcode());
        }


        consuming.putAll(instructionId, consumed.stream().map(c -> c.id).collect(Collectors.toList()));

        for (IdValue val : consumed) {
            data.putIfAbsent(val.id, val);

            consumedBy.put(val.id, instructionId);
        }
    }

    @Override
    public IdValue findById(int id) {
        return data.get(id);
    }

    @Override
    public Collection<Integer> findConsumedByInstruction(int insnNode) {
        return consuming.get(insnNode);
    }

    @Override
    public Collection<Integer> findProducedByInstruction(int insnNode) {
        return produces.get(insnNode);
    }

    @Override
    public Collection<Integer> findInstructionsConsumingValue(int value) {
        return consumedBy.get(value);
    }

    @Override
    public Integer findInstructionProducingValue(int value) {
        return producedBy.getOrDefault(value, -1);
    }

    @Override
    public void addToHeap(IdValue ref, String name, IdValue value) {
        add(values, ref, name, value);
    }

    @Override
    public void addToHeap(String klass, String name, IdValue value) {
        add(staticValues, klass, name, value);
    }

    private <K> void add(Map<K, Map<String, Stack<IdValue>>> values, K ref, String name, IdValue value) {
        if (!values.containsKey(ref)) {
            values.put(ref, new HashMap<>());
        }

        if (!values.get(ref).containsKey(name)) {
            values.get(ref).put(name, new Stack<>());
        }

        values.get(ref).get(name).push(value);
    }

    @Override
    public IdValue getFromHeap(IdValue ref, String name, IdValue defaultValue) {
        if (!values.containsKey(ref) || !values.get(ref).containsKey(name)) {
            addToHeap(ref, name, defaultValue);
        }

        return values.get(ref).get(name).peek();
    }

    @Override
    public IdValue getFromHeap(String klass, String name, IdValue defaultValue) {
        if (!staticValues.containsKey(klass) || !staticValues.get(klass).containsKey(name)) {
            addToHeap(klass, name, defaultValue);
        }

        return staticValues.get(klass).get(name).peek();
    }

    @Override
    public Collection<Integer> allValues() {
        return data.keySet();
    }

    @Override
    public void createArray(IdValue ref, IdValue proxyValue) {
        if (!arrayProxy.containsKey(ref)) {
            arrayProxy.put(ref, proxyValue);
            possibleArrayValues.put(proxyValue.id, new HashSet<>());
        }
    }

    @Override
    public void storeArray(IdValue ref, IdValue value) {
        IdValue proxyValue = arrayProxy.get(ref);
        possibleArrayValues.get(proxyValue.id).add(value.id);
        proxiedBy.put(value.id, proxyValue.id);
    }

    @Override
    public IdValue loadArray(IdValue ref) {
        return arrayProxy.get(ref);
    }

    @Override
    public Collection<Integer> expandArrayValue(int proxyValueId) {
        if (possibleArrayValues.containsKey(proxyValueId)) {
            return possibleArrayValues.get(proxyValueId);
        }

        return Collections.emptySet();
    }

    @Override
    public Integer findProxyValue(int valueId) {
        return proxiedBy.getOrDefault(valueId, -1);
    }
}
