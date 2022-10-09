package tech.jhamill34.repos;

import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.tree.MethodRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMethodRepo implements MethodRepository {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Map<Integer, MethodEntity> data = new HashMap<>();
    private final Map<Integer, NavigableMap<String, Integer>> lookup = new HashMap<>();

    @Override
    public int save(MethodEntity methodNode) {
        MethodEntity methodEntityWithId = methodNode.withId(ID_GENERATOR.getAndIncrement());

        data.put(methodEntityWithId.getId(), methodEntityWithId);

        if (!lookup.containsKey(methodEntityWithId.getOwnerId())) {
            lookup.put(methodEntityWithId.getOwnerId(), new TreeMap<>());
        }
        lookup.get(methodEntityWithId.getOwnerId()).put(methodEntityWithId.getName() + methodEntityWithId.getDescriptor(), methodEntityWithId.getId());

        return methodEntityWithId.getId();
    }

    @Override
    public int getId(int ownerId, String descriptor) {
        if (!lookup.containsKey(ownerId)) {
            return -1;
        }

        return lookup.get(ownerId).getOrDefault(descriptor, -1);
    }

    @Override
    public MethodEntity findById(int id) {
        return data.get(id);
    }

    @Override
    public Collection<Integer> allMethods() {
        return data.keySet();
    }

    @Override
    public Collection<Integer> allMethodsForOwner(int ownerId) {
        if (lookup.containsKey(ownerId)) {
            return lookup.get(ownerId).values();
        }

        return Collections.emptyList();
    }
}
