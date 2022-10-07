package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import tech.jhamill34.Application;
import tech.jhamill34.app.CLI;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new RepositoryModule());
        install(new ReportingModule());

        install(new CLIModule());

        bind(Application.class).to(CLI.class).in(Singleton.class);
    }
}
