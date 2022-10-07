package tech.jhamill34.app;

import com.google.inject.Inject;
import picocli.CommandLine;
import tech.jhamill34.Application;

public class CLI implements Application {
    private final CommandLine.IFactory factory;
    private final CLIHandler handler;

    @Inject
    public CLI(CommandLine.IFactory factory, CLIHandler handler) {
        this.factory = factory;
        this.handler = handler;
    }

    @Override
    public void start(String[] args) {
        new CommandLine(handler, factory).execute(args);
    }
}
