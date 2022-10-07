package tech.jhamill34.tree;

import com.google.common.graph.Graph;
import tech.jhamill34.entities.InstructionEntity;

import java.util.Collection;

public interface InstructionRepository {
    int save(InstructionEntity insnNode);
    void recordControlFlow(int invokerId, Graph<Integer> controlFlow);
    int getId(int invokerId, int index);
    InstructionEntity findById(int id);
    Collection<Integer> allInstructions();
    Collection<Integer> allInstructionsForInvoker(int invokerId);
    Graph<Integer> getControlFlowForInvoker(int invokerId);
}
