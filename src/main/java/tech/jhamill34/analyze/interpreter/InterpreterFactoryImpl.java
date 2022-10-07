package tech.jhamill34.analyze.interpreter;

import com.google.inject.Inject;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;
import tech.jhamill34.analyze.MethodHandler;
import tech.jhamill34.analyze.ValueContainer;
import tech.jhamill34.analyze.FieldHandler;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.analyze.InterpreterFactory;
import tech.jhamill34.analyze.HeapStore;

import java.util.List;

public class InterpreterFactoryImpl implements InterpreterFactory {
    @Inject
    private TypeInterpreter delegate;

    @Inject
    private HeapStore heapStore;

    @Inject
    private FieldHandler fieldHandler;

    @Inject
    private MethodHandler methodHandler;

    @Override
    public Interpreter<IdValue> createRecursiveInterpreter(List<? extends IdValue> parameters, ValueContainer returnValue) {
        return TrackingInterpreter.recursiveBuilder()
                .delegate(delegate)
                .heapStore(heapStore)
                .parameters(parameters)
                .returnValue(returnValue)
                .fieldHandler(fieldHandler)
                .methodHandler(methodHandler)
                .build();
    }

    @Override
    public Interpreter<IdValue> createShallowInterpreter() {
        return DelegatingInterpreter.shallowBuilder()
                .delegate(delegate)
                .build();
    }
}
