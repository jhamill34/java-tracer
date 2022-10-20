package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.entities.Entity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.commands.attributes.Query;
import tech.jhamill34.repl.commands.attributes.QueryException;
import tech.jhamill34.repl.executors.Command;

import java.util.*;

public class SortCommand implements Command {
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

        if (top instanceof Collection) {
            if (attribute != null) {
                Collection<?> items = (Collection<?>) top;
                Entity[] entities = new Entity[items.size()];

                int i = 0;
                for (Object item : items) {
                    if (item instanceof Entity) {
                        entities[i++] = (Entity) item;
                    }
                }

                try {
                    sort(entities, attribute);
                } catch (QueryException e) {
                    return e.getMessage();
                }

                stack.push(Arrays.asList(entities));
            } else {
                List<Object> items = new ArrayList<>((Collection<?>) top);
                items.sort(null);
                stack.push(items);
            }
            return "Success";
        }

        return "Invalid stack state, can only sort a list";
    }

    private void sort(Entity[] entities, String attribute) throws QueryException {
        int minIndex;

        for (int i = 0; i < entities.length - 1; i++) {
            minIndex = i;

            for (int j = i + 1; j < entities.length; j++) {
                Entity current = entities[j];
                Object currentAttr = current.accept(queryVisitor).query(attribute);

                Entity min = entities[minIndex];
                Object minAttr = min.accept(queryVisitor).query(attribute);

                @SuppressWarnings("unchecked")
                Comparable<Object> c = (Comparable<Object>) currentAttr;

                @SuppressWarnings("unchecked")
                Comparable<Object> m = (Comparable<Object>) minAttr;

                if (c.compareTo(m) < 0) {
                    minIndex = j;
                }
            }

            swap(entities, minIndex, i);
        }

        System.out.println(Arrays.toString(entities));
    }

    private void swap(Entity[] entities, int a, int b) {
        Entity tmp = entities[a];
        entities[a] = entities[b];
        entities[b] = tmp;
    }
}
