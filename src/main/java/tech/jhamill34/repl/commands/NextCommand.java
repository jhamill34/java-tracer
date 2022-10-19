package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class NextCommand implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();
        Object queue = stack.pop();

        if (queue instanceof Queue) {
            Queue<?> checkedQueue = (Queue<?>) queue;

            stack.push(checkedQueue.poll());

            return "Success";
        }

        return "Invalid stack state, can only get next value from a queue";
    }
}
