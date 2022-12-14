package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.entities.Entity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class DescribeCommand implements Command {
    @Inject
    private EntityVisitor<String> entityVisitor;

    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();
        Object top = stack.pop();

        if (top instanceof Entity) {
            Entity entity = (Entity) top;
            return entity.accept(entityVisitor);
        }

        return "Invalid stack state, expected an entity on the top of the stack: " + top;
    }
}
