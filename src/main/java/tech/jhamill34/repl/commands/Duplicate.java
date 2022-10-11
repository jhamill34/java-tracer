package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class Duplicate implements Command {

    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        stack.push(stack.peek());
        return "Success";
    }
}
