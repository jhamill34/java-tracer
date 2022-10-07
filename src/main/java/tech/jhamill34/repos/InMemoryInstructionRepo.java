package tech.jhamill34.repos;

import com.google.common.graph.Graph;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.tree.InstructionRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryInstructionRepo implements InstructionRepository {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Map<Integer, InstructionEntity> data = new HashMap<>();
    private final Map<Integer, NavigableMap<Integer, Integer>> lookup = new HashMap<>();
    private final Map<Integer, Graph<Integer>> controlFlowLookup = new HashMap<>();

    @Override
    public int save(InstructionEntity insnNode) {
        InstructionEntity insnNodeWithId = insnNode.withId(ID_GENERATOR.getAndIncrement());

        data.put(insnNodeWithId.getId(), insnNodeWithId);

        if (!lookup.containsKey(insnNodeWithId.getInvokerId())) {
            lookup.put(insnNodeWithId.getInvokerId(), new TreeMap<>());
        }
        lookup.get(insnNodeWithId.getInvokerId()).put(insnNodeWithId.getIndex(), insnNodeWithId.getId());

        return insnNodeWithId.getId();
    }

    @Override
    public void recordControlFlow(int invokerId, Graph<Integer> controlFlow) {
        controlFlowLookup.put(invokerId, controlFlow);
    }

    @Override
    public int getId(int invokerId, int index) {
        if (!lookup.containsKey(invokerId)) {
            return -1;
        }
        return lookup.get(invokerId).getOrDefault(index, -1);
    }

    @Override
    public InstructionEntity findById(int id) {
        return data.get(id);
    }

    @Override
    public Collection<Integer> allInstructions() {
        return data.keySet();
    }

    @Override
    public Collection<Integer> allInstructionsForInvoker(int invokerId) {
        return lookup.get(invokerId).values();
    }

    @Override
    public Graph<Integer> getControlFlowForInvoker(int invokerId) {
        return controlFlowLookup.get(invokerId);
    }
}
