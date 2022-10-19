package tech.jhamill34.analyze.analyzers;

import com.google.common.graph.MutableGraph;
import lombok.Builder;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Interpreter;
import tech.jhamill34.analyze.IdValue;

public class ControlFlowAnalyzer extends Analyzer<IdValue> {
    private final MutableGraph<Integer> controlFlow;

    @Builder
    public ControlFlowAnalyzer(Interpreter<IdValue> interpreter, MutableGraph<Integer> controlFlow) {
        super(interpreter);
        this.controlFlow = controlFlow;
    }

    @Override
    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
        connect(insnIndex, successorIndex);
    }

    @Override
    protected boolean newControlFlowExceptionEdge(int insnIndex, int successorIndex) {
        return true;
    }

    private void connect(int from, int to) {
        if (controlFlow != null) {
            controlFlow.addNode(from);
            controlFlow.addNode(to);
            controlFlow.putEdge(from, to);
        }
    }
}
