package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.repl.StateManager;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.FieldRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ListCommand implements Command {
    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private FieldRepository fieldRepository;

    @Inject
    private HeapStore heapStore;

    @Inject
    private StateManager stateManager;

    @Override
    public String execute(List<String> operands) {
        Stack<Object> stack = stateManager.getStack();

        char type;
        if (operands.size() == 0) {
            type = stack.pop().toString().charAt(0);
        } else {
            type = operands.get(0).charAt(0);
        }

        switch (type) {
            case 'C':
                stack.push(classRepository.allClasses().stream().map(classRepository::findById).collect(Collectors.toList()));
                break;
            case 'M':
                stack.push(methodRepository.allMethods().stream().map(methodRepository::findById).collect(Collectors.toList()));
                break;
            case 'F':
                stack.push(fieldRepository.allFields().stream().map(fieldRepository::findById).collect(Collectors.toList()));
                break;
            case 'V':
                stack.push(heapStore.allValues().stream().map(heapStore::findById).collect(Collectors.toList()));
                break;
            default:
                return "Invalid stack state, cant list for type " + type;
        }

        return "Success";
    }
}
