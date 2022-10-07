package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import picocli.CommandLine;
import tech.jhamill34.app.CLIHandler;
import tech.jhamill34.pico.ClasspathHandler;
import tech.jhamill34.pico.GuiceFactory;

public class CLIModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ClasspathModule());
        install(new REPLModule());

        bind(CommandLine.IFactory.class).to(GuiceFactory.class).in(Singleton.class);
        bind(CLIHandler.class).to(ClasspathHandler.class).in(Singleton.class);
    }
}
