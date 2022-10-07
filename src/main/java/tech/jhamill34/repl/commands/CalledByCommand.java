package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.InstructionRepository;

import java.util.List;
import java.util.Stack;

public class CalledByCommand implements Command {

    @Inject
    private InstructionRepository instructionRepository;

    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object top = stack.pop();

        if (top instanceof String) {
            String topStr = (String) top;
            char type = topStr.charAt(0);

            if (type == 'I') {
                try {
                    int instructionId = Integer.parseInt(topStr.substring(1));

                    stack.push("M" + instructionRepository.findById(instructionId).getInvokerId());
                    return "Success";
                } catch (NumberFormatException ignore) {}
            }
        } else if (top instanceof InstructionEntity) {
            InstructionEntity instructionEntity = (InstructionEntity) top;
            stack.push("M" + instructionEntity.getInvokerId());
            return "Success";
        }

        return "Invalid stack state: " + top;
    }
}
