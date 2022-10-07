package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.pico.ClasspathAnalyzer;
import tech.jhamill34.repl.executors.Command;

import java.util.List;
import java.util.Stack;

public class InvokeCommand implements Command {
    @Inject
    private ClasspathAnalyzer classpathAnalyzer;

    @Override
    public String execute(Stack<Object> stack, List<String> operands) {
        Object first = stack.pop();
        Object second = stack.pop();

        if (first instanceof MethodEntity && second instanceof ClassEntity) {
            MethodEntity methodEntity = (MethodEntity) first;
            ClassEntity classEntity = (ClassEntity) second;

            classpathAnalyzer.analyze(classEntity.getName(), methodEntity.getName() + methodEntity.getDescriptor());
            return "Success";
        }

        return "Invalid stack state: " + first + " and " + second;
    }
}
