package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Map;

public class ShowLocals implements Command {
    @Inject
    @Named("replvars")
    private Map<String, Object> locals;

    @Override
    public String execute(List<String> operands) {
        StringBuilder sb = new StringBuilder();

        locals.forEach((key, value) -> sb.append(key).append(": ").append(value).append('\n'));

        return sb.toString();
    }
}
