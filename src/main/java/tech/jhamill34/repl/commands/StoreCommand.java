package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StoreCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();
        Map<String, Object> locals = stateManager.getLocals();

        if (operands.size() > 0) {
            Object top = stack.pop();

            locals.put(operands.get(0), top);
            return "Success";
        }

        return "Must provide a key map value to";
    }
}
