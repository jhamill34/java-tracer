package tech.jhamill34.analyze;

import com.google.common.graph.MutableGraph;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Interpreter;

public interface AnalyzerFactory {
    Analyzer<IdValue> createAnalyzer(Interpreter<IdValue> interpreter);
    Analyzer<IdValue> createAnalyzer(Interpreter<IdValue> interpreter, MutableGraph<Integer> controlFlow);
}
