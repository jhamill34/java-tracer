package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.pico.REPLHandler;
import tech.jhamill34.pico.ScriptHandler;
import tech.jhamill34.repl.Compiler;
import tech.jhamill34.repl.Executor;
import tech.jhamill34.repl.SimpleREPLHandler;
import tech.jhamill34.repl.SimpleScriptHandler;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.commands.*;
import tech.jhamill34.repl.commands.attributes.Query;
import tech.jhamill34.repl.commands.attributes.QueryVisitor;
import tech.jhamill34.repl.commands.descriptions.PlainDescriptions;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.repl.executors.ExecutorImpl;
import tech.jhamill34.repl.extensions.CompilerImpl;
import tech.jhamill34.repl.state.StateManagerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class REPLModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ScriptHandler.class).to(SimpleScriptHandler.class).in(Singleton.class);
        bind(REPLHandler.class).to(SimpleREPLHandler.class).in(Singleton.class);
        bind(Executor.class).to(ExecutorImpl.class).in(Singleton.class);
        bind(Compiler.class).to(CompilerImpl.class).in(Singleton.class);

        bind(new TypeLiteral<EntityVisitor<String>>(){}).to(PlainDescriptions.class);
        bind(new TypeLiteral<EntityVisitor<Query>>(){}).to(QueryVisitor.class);
        bind(StateManager.class).to(StateManagerImpl.class).in(Singleton.class);

        MapBinder<String, Command> commandBinder = MapBinder.newMapBinder(binder(), String.class, Command.class);
        commandBinder.addBinding("stack").to(ShowStack.class).in(Singleton.class);
        commandBinder.addBinding("locals").to(ShowLocals.class).in(Singleton.class);
        commandBinder.addBinding("const").to(ShowConstants.class).in(Singleton.class);
        commandBinder.addBinding("push").to(PushCommand.class).in(Singleton.class);
        commandBinder.addBinding("pop").to(PopCommand.class).in(Singleton.class);
        commandBinder.addBinding("dup").to(Duplicate.class).in(Singleton.class);
        commandBinder.addBinding("swap").to(SwapCommand.class).in(Singleton.class);
        commandBinder.addBinding("id").to(GetId.class).in(Singleton.class);
        commandBinder.addBinding("find").to(FindCommand.class).in(Singleton.class);
        commandBinder.addBinding("invoke").to(InvokeCommand.class).in(Singleton.class);
        commandBinder.addBinding("summary").to(SummaryCommand.class).in(Singleton.class);
        commandBinder.addBinding("desc").to(DescribeCommand.class).in(Singleton.class);
        commandBinder.addBinding("load").to(LoadCommand.class).in(Singleton.class);
        commandBinder.addBinding("store").to(StoreCommand.class).in(Singleton.class);
        commandBinder.addBinding("attr").to(GetAttributeCommand.class).in(Singleton.class);
        commandBinder.addBinding("expand").to(ExpandCommand.class).in(Singleton.class);
        commandBinder.addBinding("math").to(MathCommand.class).in(Singleton.class);
        commandBinder.addBinding("cmp").to(CompareCommand.class).in(Singleton.class);
        commandBinder.addBinding("queue").to(QueueCommand.class).in(Singleton.class);
        commandBinder.addBinding("next").to(NextCommand.class).in(Singleton.class);
        commandBinder.addBinding("offer").to(EnqueueCommand.class).in(Singleton.class);
        commandBinder.addBinding("list").to(ListCommand.class).in(Singleton.class);
    }
}
