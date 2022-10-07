package tech.jhamill34.repl.commands;

import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class PopCommand implements Command {
    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
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
