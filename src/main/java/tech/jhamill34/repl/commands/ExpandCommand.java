package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class ExpandCommand implements Command {
    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        Object top = stack.pop();

        if (top instanceof List) {
            List<?> items = (List<?>) top;

            for (Object item : items) {
                stack.push(item);
            }

            stack.push(items.size());

            return "Success";
        }

        return "Invalid stack state, expected list reference on top: " + top;
    }
}
