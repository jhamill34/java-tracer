package tech.jhamill34.repl;

import java.util.List;

public interface Executor {
    String execute(String input, List<String> operands);
}
