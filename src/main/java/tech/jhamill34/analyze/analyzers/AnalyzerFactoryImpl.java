package tech.jhamill34.analyze.analyzers;

import com.google.common.graph.MutableGraph;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Interpreter;
import tech.jhamill34.analyze.AnalyzerFactory;
import tech.jhamill34.analyze.IdValue;

public class AnalyzerFactoryImpl implements AnalyzerFactory {
    @Override
    public Analyzer<IdValue> createAnalyzer(Interpreter<IdValue> interpreter, MutableGraph<Integer> controlFlow) {
        return ControlFlowAnalyzer.builder()
                .interpreter(interpreter)
                .controlFlow(controlFlow)
                .build();
    }

    @Override
    public Analyzer<IdValue> createAnalyzer(Interpreter<IdValue> interpreter) {
        return ControlFlowAnalyzer.builder()
                .interpreter(interpreter)
                .build();
    }
}
