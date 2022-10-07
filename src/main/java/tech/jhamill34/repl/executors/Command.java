package tech.jhamill34.repl.executors;

import java.util.List;
import java.util.Stack;

@FunctionalInterface
public interface Command {
    String execute(Stack<Object> stack, List<String> operands);
}
