package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class ExpandCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        Object top = stack.pop();

        if (top instanceof List) {
            List<?> items = (List<?>) top;

            for (int i = items.size() - 1; i >= 0; i--) {
                stack.push(items.get(i));
            }

            stack.push(items.size());

            return "Success";
        }

        return "Invalid stack state, expected list reference on top: " + top;
    }
}
