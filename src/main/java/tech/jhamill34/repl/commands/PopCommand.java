package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class PopCommand implements Command {

    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        int count = 1;
        if (operands.size() > 0) {
            try {
                count = Integer.parseInt(operands.get(0));
            } catch (NumberFormatException ignored) {}
        }

        for (int i = 0; i < count; i++) {
            stack.pop();
        }

        return "Removed " + count + " items";
    }
}
