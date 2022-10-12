package tech.jhamill34.repl.state;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Value
public class Frame {
    Stack<Object> stack;
    Map<String, Object> locals;
    int returnAddress;

    public Frame(int returnAddress) {
        this.stack = new Stack<>();
        this.locals = new HashMap<>();
        this.returnAddress = returnAddress;
    }
}
