package tech.jhamill34.repl.executors;

import com.google.inject.Inject;
import tech.jhamill34.repl.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExecutorImpl implements Executor {
    private static final String DELIM = ":";

    @Inject
    private Map<String, Command> commands;

    private final Stack<Object> stack = new Stack<>();

    @Override
    public String execute(String input) {
        String[] parts = input.split(DELIM);
        String cmd = parts[0].trim();

        List<String> operands = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            operands.add(parts[i].trim());
        }

        if (commands.containsKey(cmd)) {
            return commands.get(cmd).execute(stack, operands);
        }

        return "Unknown command: " + input;
    }
}
