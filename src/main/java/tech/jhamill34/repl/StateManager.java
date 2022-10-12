package tech.jhamill34.repl;

import java.util.Map;
import java.util.Stack;

public interface StateManager {
    void push(int returnAddress, int argc);
    int pop();
    Stack<Object> getStack();
    Map<String, Object> getLocals();
}
