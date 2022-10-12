package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class PushCommand implements Command {

    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        for (String operand : operands) {
            try {
                int number = Integer.parseInt(operand);
                stack.push(number);
            } catch (NumberFormatException e) {
                stack.push(operand);
            }
        }

        return "Added " + operands.size() + " items to stack";
    }
}
