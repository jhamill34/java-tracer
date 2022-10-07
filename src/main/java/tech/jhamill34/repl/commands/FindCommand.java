package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.InstructionRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.List;
import java.util.Stack;

public class FindCommand implements Command {
    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private InstructionRepository instructionRepository;

    @Inject
    private HeapStore heapStore;

    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object top = stack.pop();

        if (top instanceof String) {
            String topString = (String) top;
            char type = topString.charAt(0);
            String idString = topString.substring(1);

            try {
                int id = Integer.parseInt(idString);

                if (type == 'C') {
                    stack.push(classRepository.findById(id));
                } else if (type == 'M') {
                    stack.push(methodRepository.findById(id));
                } else if (type == 'I') {
                    stack.push(instructionRepository.findById(id));
                } else if (type == 'V') {
                    stack.push(heapStore.findById(id));
                } else {
                    return "Invalid stack state, invalid type: " + top;
                }
                return "Success";
            } catch (NumberFormatException ignore) {}
        }

        return "Inavlid stack state: " + top;
    }
}
