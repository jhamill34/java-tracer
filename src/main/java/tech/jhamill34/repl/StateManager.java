package tech.jhamill34.repl;

import java.util.Map;
import java.util.Stack;

public interface StateManager {
    String push(int returnAddress, int argc);
    int pop();
    Stack<Object> getStack();
    Map<String, Object> getLocals();
    Map<String, Object> getConstants();
}
