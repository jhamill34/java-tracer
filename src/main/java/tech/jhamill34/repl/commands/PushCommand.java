package tech.jhamill34.repl.commands;

import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class PushCommand implements Command {
    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        for (String operand : operands) {
            stack.push(operand);
        }

        return "Added " + operands.size() + " items to stack";
    }
}
