package tech.jhamill34.repl.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.repl.executors.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class GetAttributeCommand implements Command {
    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public String execute(List<String> operands) {
        if (operands.size() > 0) {
            Object top = stack.pop();
            String attribute = operands.get(0);

            String getter = "get" + attribute.substring(0, 1).toUpperCase(Locale.ROOT) + attribute.substring(1);

            try {
                Method method = top.getClass().getDeclaredMethod(getter);
                Object result = method.invoke(top);
                stack.push(result);
                return "Success";
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return "Attribute not found: " + attribute;
            }

        }

        return "Must provide attribute to find";
    }
}
