package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LoadCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();
        Map<String, Object> locals = stateManager.getLocals();

        if (operands.size() > 0) {
            if (locals.containsKey(operands.get(0))) {
                stack.push(locals.get(operands.get(0)));
                return "Success";
            }

            return "Key not found in locals: " + operands.get(0);
        }

        return "Must provide a key to laod from";
    }
}
