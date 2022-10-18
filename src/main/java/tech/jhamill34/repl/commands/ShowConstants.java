package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Map;

public class ShowConstants implements Command {
    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Map<String, Object> locals = stateManager.getConstants();

        StringBuilder sb = new StringBuilder();

        locals.forEach((key, value) -> sb.append(key).append(": ").append(value).append('\n'));

        return sb.toString();
    }
}
