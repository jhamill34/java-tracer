package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.FieldRepository;
import tech.jhamill34.tree.InstructionRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class SummaryCommand implements Command {
    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private InstructionRepository instructionRepository;

    @Inject
    private FieldRepository fieldRepository;

    @Inject
    private HeapStore heapStore;

    @Override
    public String execute(List<String> operands) {
        StringBuilder sb = new StringBuilder();
        Collection<Integer> classIds = classRepository.allClasses();
        Collection<Integer> methodIds = methodRepository.allMethods();
        Collection<Integer> fieldIds = fieldRepository.allFields();
        Collection<Integer> instructionIds = instructionRepository.allInstructions();
        Collection<Integer> variableIds = heapStore.allValues();

        sb.append("Loaded Classes: ").append(classIds.size()).append('\n');
        sb.append("Loaded Methods: ").append(methodIds.size()).append('\n');
        sb.append("Loaded Fields: ").append(fieldIds.size()).append('\n');
        sb.append("Loaded Instructions: ").append(instructionIds.size()).append('\n');

        sb.append("Variables: ").append(variableIds.size()).append('\n');
        for (int variableId : variableIds) {
            sb.append('\t').append(variableId).append('\n');
        }

        return sb.toString();
    }
}
