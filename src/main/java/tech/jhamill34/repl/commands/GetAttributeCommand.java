package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.entities.Entity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.repl.commands.attributes.Query;
import tech.jhamill34.repl.commands.attributes.QueryException;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class GetAttributeCommand implements Command {
    @Inject
    private EntityVisitor<Query> queryVisitor;

    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        if (operands.size() > 0) {
            Object top = stack.pop();
            String attribute = operands.get(0);

            if (top instanceof Entity) {
                Entity entity = (Entity) top;
                try {
                    stack.push(entity.accept(queryVisitor).query(attribute));
                    return "Success";
                } catch (QueryException e) {
                    return e.getMessage();
                }
            } else if (top instanceof List) {
                List<?> items = (List<?>) top;
                try {
                    int index = Integer.parseInt(attribute);
                    stack.push(items.get(index));
                    return "Success";
                } catch (NumberFormatException e) {
                    return "Indexing into an array requires an integer: " + attribute;
                }
            }
        }

        return "Must provide attribute to find";
    }
}
