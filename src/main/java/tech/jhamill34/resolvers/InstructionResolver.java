package tech.jhamill34.resolvers;

import com.google.inject.Inject;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.entities.EntityUtilities;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.tree.FieldRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.List;
import java.util.stream.Collectors;

public class InstructionResolver {
    @Inject
    private HeapStore heapStore;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private FieldRepository fieldRepository;


    public List<IdValue> getProduced(InstructionEntity parent) {
        return heapStore.findProducedByInstruction(parent.getId())
                .stream()
                .map(id -> heapStore.findById(id))
                .collect(Collectors.toList());
    }

    public List<IdValue> getConsumed(InstructionEntity parent) {
        return heapStore.findConsumedByInstruction(parent.getId())
                .stream()
                .map(id -> heapStore.findById(id))
                .collect(Collectors.toList());
    }

    public MethodEntity getInvoker(InstructionEntity parent) {
        return methodRepository.findById(parent.getInvokerId());
    }

    public Object getReference(InstructionEntity parent) {
        if (parent.getReferenceId() >= 0) {
            switch (parent.getReferenceType()) {
                case InstructionEntity.METHOD:
                    return methodRepository.findById(parent.getReferenceId());
                case InstructionEntity.FIELD:
                    return fieldRepository.findById(parent.getReferenceId());
            }
        }

        return null;
    }

    public String getOpCodeName(InstructionEntity parent) {
        return EntityUtilities.convertOpcode(parent.getOpCode());
    }
}
