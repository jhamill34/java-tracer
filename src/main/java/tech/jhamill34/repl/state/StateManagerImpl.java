package tech.jhamill34.repl.state;

import tech.jhamill34.repl.StateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StateManagerImpl implements StateManager {
    private final Stack<Object> stack = new Stack<>();
    private final Map<String, Object> locals = new HashMap<>();

    @Override
    public void push() {

    }

    @Override
    public void pop() {

    }

    @Override
    public Stack<Object> getStack() {
        return stack;
    }

    @Override
    public Map<String, Object> getLocals() {
        return locals;
    }
}
