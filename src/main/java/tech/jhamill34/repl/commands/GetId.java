package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.Entity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.InstructionRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.List;
import java.util.Stack;

public class GetId implements Command {
    private static final Logger logger = LoggerFactory.getLogger(GetId.class);

    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private InstructionRepository instructionRepository;

    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        char type;
        if (operands.size() == 0) {
            type = stack.pop().toString().charAt(0);
        } else {
            type = operands.get(0).charAt(0);
        }

        Object top = stack.pop();
        if (top instanceof String) {
            String identifier = (String) top;

            if (type == 'C') {
                int id = classRepository.getId(identifier);
                if (id < 0) {
                    return "Not found: " + identifier;
                } else {
                    stack.push(id);
                }
            } else if (type == 'M') {
                Object next = stack.pop();
                if (next instanceof ClassEntity) {
                    ClassEntity classEntity = (ClassEntity) next;
                    int id = methodRepository.getId(classEntity.getId(), identifier);
                    if (id < 0) {
                        return "Not found: " + classEntity.getId() + " method identifier " + identifier;
                    } else {
                        stack.push(id);
                    }
                } else {
                    return "Invalid stack state, expected class entity after method id: " + next;
                }
            } else if (type == 'i') {
                Object next = stack.pop();
                if (next instanceof MethodEntity) {
                    MethodEntity methodEntity = (MethodEntity) next;
                    try {
                        int index = Integer.parseInt(identifier);
                        int id = instructionRepository.getId(methodEntity.getId(), index);
                        if (id < 0) {
                           return "Not found: " + methodEntity.getName() + " instruction at index " + index;
                        } else {
                            stack.push(id);
                        }
                    } catch (NumberFormatException e) {
                        return "Invalid stack state, expected index to be a number: " + identifier;
                    }
                } else {
                    return "Invalid stack state, expected method entity after index: " + next;
                }
            } else {
                return "Invalid type: " + type;
            }

            return "Success";
        } else if (top instanceof Entity) {
            Entity entity = (Entity) top;
            stack.push(entity.getId());
            return "Success";
        }

        return "Invalid stack state: " + top;
    }
}
