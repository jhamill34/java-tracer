package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StoreCommand implements Command {
    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Inject
    @Named("replvars")
    private Map<String, Object> locals;

    @Override
    public String execute(List<String> operands) {
        if (operands.size() > 0) {
            Object top = stack.pop();

            locals.put(operands.get(0), top);
            return "Success";
        }

        return "Must provide a key map value to";
    }
}
