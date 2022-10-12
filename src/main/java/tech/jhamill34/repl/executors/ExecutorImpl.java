package tech.jhamill34.repl.executors;

import com.google.inject.Inject;
import tech.jhamill34.repl.Executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExecutorImpl implements Executor {
    @Inject
    private Map<String, Command> commands;

    @Override
    public String execute(String cmd, List<String> operands) {
        if (commands.containsKey(cmd)) {
            return commands.get(cmd).execute(operands);
        }

        if (cmd.isEmpty()) {
            return "";
        }

        return "Unknown command: " + cmd;
    }
}
