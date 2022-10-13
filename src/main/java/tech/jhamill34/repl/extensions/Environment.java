package tech.jhamill34.repl.extensions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class Environment {
    private static int current = 0;
    final Environment parent;
    final Map<String, Integer> values = new HashMap<>();

    public void declare(String identifier) {
        values.put(identifier, current++);
    }

    public int find(String identifier) {
        if (values.containsKey(identifier)) {
            return values.get(identifier);
        }

        if (parent != null) {
            return parent.find(identifier);
        }

        return -1;
    }
}
