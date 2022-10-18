package tech.jhamill34.repl.state;

import tech.jhamill34.repl.StateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StateManagerImpl implements StateManager {
    private final Stack<Frame> frames;
    private final Map<String, Object> constants;

    public StateManagerImpl() {
        this.frames = new Stack<>();
        this.constants = new HashMap<>();
    }

    @Override
    public String push(int returnAddress, int argc) {
        String label = null;

        if (argc == 0) {
            if (!this.frames.isEmpty()) {
                label = (String) getStack().pop();
            }
            this.frames.push(new Frame(returnAddress));
        } else {
            // Get previous stack
            Stack<Object> stack = getStack();

            // Copy the top N items on stack
            Object[] args = new Object[argc];
            for (int i = argc - 1; i >= 0; i--) {
                args[i] = stack.pop();
            }
            label = (String) stack.pop();

            // Create new stack
            this.frames.push(new Frame(returnAddress));

            // Get new stack
            stack = getStack();

            // Copy N items to new stack, effectively passing arguments
            for (int i = 0; i < argc; i++) {
                stack.push(args[i]);
            }
        }

        return label;
    }

    @Override
    public int pop() {
        Stack<Object> stack = getStack();

        if (stack.empty()) {
            return this.frames.pop().getReturnAddress();
        }

        Object returnValue = stack.pop();
        int returnAddress = this.frames.pop().getReturnAddress();

        stack = getStack();
        stack.push(returnValue);

        return returnAddress;
    }

    @Override
    public Stack<Object> getStack() {
        return frames.peek().getStack();
    }

    @Override
    public Map<String, Object> getLocals() {
        return frames.peek().getLocals();
    }

    @Override
    public Map<String, Object> getConstants() {
        return constants;
    }
}
