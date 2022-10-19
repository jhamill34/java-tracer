package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.entities.Entity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.commands.attributes.Query;
import tech.jhamill34.repl.commands.attributes.QueryException;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class GetAttributeCommand implements Command {
    @Inject
    private EntityVisitor<Query> queryVisitor;

    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        String attribute;
        if (operands.size() > 0) {
            attribute = operands.get(0);
        } else {
            attribute = stack.pop().toString();
        }

        Object top = stack.pop();
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

            if (attribute.equals("size")) {
                stack.push(items.size());
                return "Success";
            }

            try {
                int index = Integer.parseInt(attribute);
                stack.push(items.get(index));
                return "Success";
            } catch (NumberFormatException e) {
                return "Indexing into an array requires an integer: " + attribute;
            }
        }

        return "Invalid stack state, can only get attributes on a list or entity: " + top;
    }
}
