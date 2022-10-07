package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
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

    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object top = stack.pop();

        if (top instanceof String) {
            String searchString = (String) top;
            char type = searchString.charAt(0);
            String identifier = searchString.substring(1);

            if (type == 'C') {
                int id = classRepository.getId(identifier);
                if (id < 0) {
                    return "Not found: " + identifier;
                } else {
                    stack.push("C" + id);
                }
            } else if (type == 'M') {
                Object next = stack.pop();
                if (next instanceof ClassEntity) {
                    ClassEntity classEntity = (ClassEntity) next;
                    int id = methodRepository.getId(classEntity.getId(), identifier);
                    if (id < 0) {
                        return "Not found: " + classEntity.getId() + " method identifier " + identifier;
                    } else {
                        stack.push("M" + id);
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
                            stack.push("I" + id);
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

            String type = entity.accept(new EntityVisitor() {
                @Override
                public String visitClassEntity(ClassEntity classEntity) {
                    return "C";
                }

                @Override
                public String visitInstructionEntity(InstructionEntity instructionEntity) {
                    return "I";
                }

                @Override
                public String visitMethodEntity(MethodEntity methodEntity) {
                    return "M";
                }

                @Override
                public String visitFieldEntity(FieldEntity fieldEntity) {
                    return "F";
                }

                @Override
                public String visitValue(IdValue value) {
                    return "V";
                }
            });

            stack.push(type + entity.getId());
            return "Success";
        }

        return "Invalid stack state: " + top;
    }
}
