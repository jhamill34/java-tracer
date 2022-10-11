package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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

    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        if (operands.size() == 0) {
            return "Must provide entity type";
        }

        Object top = stack.pop();
        char type = operands.get(0).charAt(0);
        int id = -1;

        if (top instanceof String) {
            String idString = (String) top;
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                return "Invalid stack state, must provide a number: " + idString;
            }
        } else if (top instanceof Integer) {
            id = (Integer) top;
        } else {
            return "Invalid stack state: " + top;
        }

        if (type == 'C') {
            stack.push(classRepository.findById(id));
        } else if (type == 'M') {
            stack.push(methodRepository.findById(id));
        } else if (type == 'I') {
            stack.push(instructionRepository.findById(id));
        } else if (type == 'V') {
            stack.push(heapStore.findById(id));
        } else {
            return "Invalid stack state, invalid type: " + type;
        }

        return "Success";
    }
}
