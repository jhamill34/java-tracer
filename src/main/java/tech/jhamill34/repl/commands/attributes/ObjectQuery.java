package tech.jhamill34.repl.commands.attributes;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

@RequiredArgsConstructor(staticName = "of")
public class ObjectQuery implements Query {
    private final Object obj;
    private final Object resolver;

    @Override
    public Object query(String input) throws QueryException {
        String getter = "get" + input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);

        try {
            Method method = obj.getClass().getDeclaredMethod(getter);
            return method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new QueryException("Attribute not found, can't access method on entity: " + input, e);
        } catch (NoSuchMethodException e) {
            if (resolver == null) {
                throw new QueryException("Attribute not found, no resolver: " + input, e);
            }
        }

        try {
            Method method = resolver.getClass().getDeclaredMethod(getter, obj.getClass());

            return method.invoke(resolver, obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new QueryException("Attribute not found, resolver doesn't have property: " + input, e);
        }
    }
}
