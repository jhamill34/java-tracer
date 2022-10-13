package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class CompareCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        if (operands.size() == 0) {
            return "Expected a comparison operator";
        }

        String operator = operands.get(0);
        Object first = stack.pop();
        Object second = stack.pop();

        if (operator.equals("=")) {
           stack.push(first.equals(second));
        } else if (operator.equals("!")) {
            stack.push(!first.equals(second));
        } else {
            try {
                int a = Integer.parseInt(first.toString());
                int b = Integer.parseInt(second.toString());

                switch (operator) {
                    case "<":
                        stack.push(b < a);
                        break;
                    case ">":
                        stack.push(b > a);
                        break;
                    case "<=":
                        stack.push(b <= a);
                        break;
                    case ">=":
                        stack.push(b >= a);
                        break;
                    default:
                        return "Unknown operator: " + operator;
                }
            } catch (NumberFormatException e) {
                return "Expected two numbers " + first + ", " + second;
            }
        }

        return "Success";
    }
}
