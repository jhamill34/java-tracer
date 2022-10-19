package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class EnqueueCommand implements Command {
    @Inject
    private StateManager stateManager;

    @SuppressWarnings("unchecked")
    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        Object value = stack.pop();
        Object queue = stack.pop();

        if (queue instanceof Queue) {
            Queue<Object> checkedQueue = (Queue<Object>) queue;
            checkedQueue.offer(value);
            stack.push(checkedQueue);

            return "Success";
        }

        return "Invalid stack state, can only enqueue to a queue type";
    }
}
