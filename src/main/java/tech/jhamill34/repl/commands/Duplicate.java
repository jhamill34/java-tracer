package tech.jhamill34.repl.commands;

import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class Duplicate implements Command {
    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        stack.push(stack.peek());
        return "Success";
    }
}
