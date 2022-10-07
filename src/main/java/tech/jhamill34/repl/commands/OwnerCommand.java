package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.MethodRepository;

import java.util.List;
import java.util.Stack;

public class OwnerCommand implements Command {
    @Inject
    private MethodRepository methodRepository;

    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object top = stack.pop();

        if (top instanceof String) {
            String topStr = (String) top;
            char type = topStr.charAt(0);

            if (type == 'M') {
                try {
                    int methodId = Integer.parseInt(topStr.substring(1));

                    stack.push("C" + methodRepository.findById(methodId).getOwnerId());
                    return "Success";
                } catch (NumberFormatException ignore) {}
            }
        } else if (top instanceof MethodEntity) {
            MethodEntity methodEntity = (MethodEntity) top;
            stack.push("C" + methodEntity.getOwnerId());
            return "Success";
        }

        return "Invalid stack state: " + top;
    }
}
