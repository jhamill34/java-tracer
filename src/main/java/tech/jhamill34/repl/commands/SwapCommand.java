package tech.jhamill34.repl.commands;

import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class SwapCommand implements Command {
    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object first = stack.pop();
        Object second = stack.pop();

        stack.push(first);
        stack.push(second);

        return "Success";
    }
}
