package tech.jhamill34.repos;

import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.tree.FieldRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryFieldRepo implements FieldRepository {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Map<Integer, FieldEntity> data = new HashMap<>();
    private final Map<Integer, NavigableMap<String, Integer>> lookup = new HashMap<>();


    @Override
    public int save(FieldEntity fieldEntity) {
        FieldEntity fieldEntityWithId = fieldEntity.withId(ID_GENERATOR.getAndIncrement());

        data.put(fieldEntityWithId.getId(), fieldEntityWithId);

        if (!lookup.containsKey(fieldEntityWithId.getOwnerId())) {
            lookup.put(fieldEntityWithId.getOwnerId(), new TreeMap<>());
        }
        lookup.get(fieldEntityWithId.getOwnerId()).put(fieldEntityWithId.getName() + fieldEntityWithId.getDescriptor(), fieldEntityWithId.getId());

        return fieldEntityWithId.getId();
    }

    @Override
    public int getId(int ownerId, String name) {
        if (!lookup.containsKey(ownerId)) {
            return -1;
        }

        return lookup.get(ownerId).getOrDefault(name, -1);
    }

    @Override
    public FieldEntity findById(int id) {
        return data.get(id);
    }

    @Override
    public Collection<Integer> allFields() {
        return data.keySet();
    }

    @Override
    public Collection<Integer> allFieldsForOwner(int ownerId) {
        if (lookup.containsKey(ownerId)) {
            return lookup.get(ownerId).values();
        }

        return Collections.emptyList();
    }
}
