package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class MathCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        if (operands.size() == 0) {
            return "Expected math operator as operand";
        }

        char operand = operands.get(0).charAt(0);
        Object first = stack.pop();
        Object second = stack.pop();

        try {
            int a = Integer.parseInt(first.toString());
            int b = Integer.parseInt(second.toString());

            switch(operand) {
                case '+':
                    stack.push(b + a);
                    break;
                case '-':
                    stack.push(b - a);
                    break;
                case '*':
                    stack.push(b * a);
                    break;
                case '/':
                    stack.push(b / a);
                    break;
                default:
                    return "Unknown operator: " + operand;
            }

            return "Success";
        } catch (NumberFormatException e) {
            return "Invalid stack state, expected top two values to be numbers: " + first + ", " + second;
        }
    }
}
