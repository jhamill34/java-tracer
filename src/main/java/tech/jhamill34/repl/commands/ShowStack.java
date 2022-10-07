package tech.jhamill34.repl.commands;

import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class ShowStack implements Command {
    @Override
    public String execute(Stack<Object> args, List<String> operands) {
        StringBuilder sb = new StringBuilder();
        args.forEach(item -> sb.append(item).append('\n'));
        return sb.toString();
    }
}
