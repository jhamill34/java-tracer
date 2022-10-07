package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.repl.executors.Command;
import tech.jhamill34.tree.ClassRepository;

import java.util.List;
import java.util.Stack;

public class PackageCommand implements Command {
    @Inject
    private ClassRepository classRepository;

    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object top = stack.pop();

        if (top instanceof String) {
            String topStr = (String) top;
            char type = topStr.charAt(0);
            String idString = topStr.substring(1);

            if (type == 'C') {
               try {
                   int classId = Integer.parseInt(idString);
                   return classRepository.findById(classId).getPackageName();
               } catch (NumberFormatException ignore) {}
            }
        } else if (top instanceof ClassEntity) {
            ClassEntity classEntity = (ClassEntity) top;
            return classEntity.getPackageName();
        }

        return "Invalid Stack State: " + top;
    }
}
