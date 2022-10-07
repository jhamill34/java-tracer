package tech.jhamill34.repos;

import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.tree.ClassRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryClassRepo implements ClassRepository {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Map<Integer, ClassEntity> data = new HashMap<>();
    private final Map<String, Integer> lookup = new HashMap<>();

    @Override
    public int save(ClassEntity classNode) {
        ClassEntity classEntityWithId = classNode.withId(ID_GENERATOR.getAndIncrement());

        data.put(classEntityWithId.getId(), classEntityWithId);
        lookup.put(classEntityWithId.getName(), classEntityWithId.getId());

        return classEntityWithId.getId();
    }

    @Override
    public ClassEntity findById(int id) {
        return data.get(id);
    }

    @Override
    public int getId(String name) {
        return lookup.getOrDefault(name, -1);
    }

    @Override
    public Collection<Integer> allClasses() {
        return data.keySet();
    }
}
