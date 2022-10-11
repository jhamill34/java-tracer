package tech.jhamill34.resolvers;

import com.google.inject.Inject;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.tree.InstructionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ValueResolver {
    @Inject
    private HeapStore heapStore;

    @Inject
    private InstructionRepository instructionRepository;

    public List<InstructionEntity> getProducers(IdValue parent) {
        return heapStore.findInstructionProducingValue(parent.getId())
                .stream()
                .map(id -> instructionRepository.findById(id))
                .collect(Collectors.toList());
    }

    public List<InstructionEntity> getConsumers(IdValue parent) {
        return heapStore.findInstructionsConsumingValue(parent.getId())
                .stream()
                .map(id -> instructionRepository.findById(id))
                .collect(Collectors.toList());
    }

    public List<IdValue> getProxies(IdValue parent) {
        return heapStore.expandArrayValue(parent.getId())
                .stream()
                .map(id -> heapStore.findById(id))
                .collect(Collectors.toList());
    }

    public IdValue getProxiedBy(IdValue parent) {
        int proxyId = heapStore.findProxyValue(parent.getId());

        if (proxyId >= 0) {
            return heapStore.findById(proxyId);
        }

        return null;
    }
}
