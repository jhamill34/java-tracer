package tech.jhamill34.analyze;

import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

public interface InterpreterFactory {
    Interpreter<IdValue> createRecursiveInterpreter(List<? extends IdValue> parameters, ValueContainer returnValue);
    Interpreter<IdValue> createShallowInterpreter();
}
