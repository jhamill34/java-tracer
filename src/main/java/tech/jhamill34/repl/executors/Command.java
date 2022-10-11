package tech.jhamill34.repl.executors;

import java.util.List;

@FunctionalInterface
public interface Command {
    String execute(List<String> operands);
}
