package tech.jhamill34.resolvers;

import com.google.common.graph.Graph;
import com.google.inject.Inject;
import org.objectweb.asm.Type;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.EntityUtilities;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.InstructionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodResolver {
    @Inject
    private InstructionRepository instructionRepository;

    @Inject
    private ClassRepository classRepository;

    public Integer getComplexity(MethodEntity parent) {
        Graph<Integer> controlFlow = instructionRepository.getControlFlowForInvoker(parent.getId());

        if (controlFlow == null) {
            return -1;
        }

        int edges = controlFlow.edges().size();
        int nodes = controlFlow.nodes().size();

        return edges - nodes + 2;
    }

    public List<InstructionEntity> getInstructions(MethodEntity parent) {
        return instructionRepository.allInstructionsForInvoker(parent.getId())
                .stream()
                .map(id -> instructionRepository.findById(id))
                .collect(Collectors.toList());
    }

    public ClassEntity getOwner(MethodEntity parent) {
        return classRepository.findById(parent.getOwnerId());
    }

    public String getReturnType(MethodEntity methodEntity) {
        return EntityUtilities.convertReturnType(methodEntity);
    }

    public List<String> getParameters(MethodEntity methodEntity) {
        return EntityUtilities.convertArguments(methodEntity);
    }

    public List<String> getAccessList(MethodEntity methodEntity) {
        return EntityUtilities.convertAccessList(methodEntity.getAccess());
    }
}
