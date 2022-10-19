package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class QueueCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        if (operands.size() > 0 && operands.get(0).equals("L")) {
            Object top = stack.pop();
            if (top instanceof Collection) {
                Collection<?> items = (Collection<?>) top;
                stack.push(new LinkedList<>(items));
                return "Success";
            }
        }

        stack.push(new LinkedList<>());

        return "Success";
    }
}
