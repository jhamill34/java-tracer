package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.pico.REPLHandler;
import tech.jhamill34.repl.Executor;
import tech.jhamill34.repl.SimpleREPLHandler;
import tech.jhamill34.repl.commands.DescribeCommand;
import tech.jhamill34.repl.commands.Duplicate;
import tech.jhamill34.repl.commands.FindCommand;
import tech.jhamill34.repl.commands.GetId;
import tech.jhamill34.repl.commands.CalledByCommand;
import tech.jhamill34.repl.commands.InvokeCommand;
import tech.jhamill34.repl.commands.LineNumberCommand;
import tech.jhamill34.repl.commands.OwnerCommand;
import tech.jhamill34.repl.commands.PackageCommand;
import tech.jhamill34.repl.commands.PopCommand;
import tech.jhamill34.repl.commands.PushCommand;
import tech.jhamill34.repl.commands.ShowStack;
import tech.jhamill34.repl.commands.SummaryCommand;
import tech.jhamill34.repl.commands.SwapCommand;
import tech.jhamill34.repl.commands.descriptions.PlainDescriptions;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.repl.executors.ExecutorImpl;

public class REPLModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(REPLHandler.class).to(SimpleREPLHandler.class).in(Singleton.class);
        bind(Executor.class).to(ExecutorImpl.class).in(Singleton.class);
        bind(EntityVisitor.class).to(PlainDescriptions.class);

        MapBinder<String, Command> commandBinder = MapBinder.newMapBinder(binder(), String.class, Command.class);
        commandBinder.addBinding("show").to(ShowStack.class).in(Singleton.class);
        commandBinder.addBinding("push").to(PushCommand.class).in(Singleton.class);
        commandBinder.addBinding("pop").to(PopCommand.class).in(Singleton.class);
        commandBinder.addBinding("dup").to(Duplicate.class).in(Singleton.class);
        commandBinder.addBinding("swap").to(SwapCommand.class).in(Singleton.class);
        commandBinder.addBinding("id").to(GetId.class).in(Singleton.class);
        commandBinder.addBinding("pkg").to(PackageCommand.class).in(Singleton.class);
        commandBinder.addBinding("find").to(FindCommand.class).in(Singleton.class);
        commandBinder.addBinding("caller").to(CalledByCommand.class).in(Singleton.class);
        commandBinder.addBinding("owner").to(OwnerCommand.class).in(Singleton.class);
        commandBinder.addBinding("line").to(LineNumberCommand.class).in(Singleton.class);
        commandBinder.addBinding("invoke").to(InvokeCommand.class).in(Singleton.class);
        commandBinder.addBinding("summary").to(SummaryCommand.class).in(Singleton.class);
        commandBinder.addBinding("desc").to(DescribeCommand.class).in(Singleton.class);
    }
}
