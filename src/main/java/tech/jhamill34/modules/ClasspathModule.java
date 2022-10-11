package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import tech.jhamill34.analyze.ArrayHandler;
import tech.jhamill34.analyze.MethodHandler;
import tech.jhamill34.analyze.AnalyzerFactory;
import tech.jhamill34.analyze.ClasspathSimulationAnalylzer;
import tech.jhamill34.analyze.FieldHandler;
import tech.jhamill34.analyze.InterpreterFactory;
import tech.jhamill34.analyze.analyzers.AnalyzerFactoryImpl;
import tech.jhamill34.analyze.handlers.ArrayHandlerImpl;
import tech.jhamill34.analyze.handlers.FieldHandlerImpl;
import tech.jhamill34.analyze.handlers.MethodHandlerImpl;
import tech.jhamill34.analyze.interpreter.InterpreterFactoryImpl;
import tech.jhamill34.file.BytecodeProcessor;
import tech.jhamill34.file.BytecodeTreeProcessor;
import tech.jhamill34.path.ClasspathProcessor;
import tech.jhamill34.path.FileProcessor;
import tech.jhamill34.pico.ClasspathAnalyzer;
import tech.jhamill34.pico.PathProcessor;
import tech.jhamill34.tree.ASMTreeProcessor;

public class ClasspathModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PathProcessor.class).to(ClasspathProcessor.class).in(Singleton.class);
        bind(FileProcessor.class).to(BytecodeProcessor.class).in(Singleton.class);
        bind(BytecodeTreeProcessor.class).to(ASMTreeProcessor.class).in(Singleton.class);
        bind(ClasspathAnalyzer.class).to(ClasspathSimulationAnalylzer.class).in(Singleton.class);

        bind(AnalyzerFactory.class).to(AnalyzerFactoryImpl.class).in(Singleton.class);
        bind(InterpreterFactory.class).to(InterpreterFactoryImpl.class).in(Singleton.class);

        bind(FieldHandler.class).to(FieldHandlerImpl.class).in(Singleton.class);
        bind(MethodHandler.class).to(MethodHandlerImpl.class).in(Singleton.class);
        bind(ArrayHandler.class).to(ArrayHandlerImpl.class).in(Singleton.class);
    }
}
