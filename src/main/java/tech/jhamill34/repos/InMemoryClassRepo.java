package tech.jhamill34.repos;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.tree.ClassRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryClassRepo implements ClassRepository {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final Map<Integer, ClassEntity> data = new HashMap<>();
    private final Map<String, Integer> lookup = new HashMap<>();
    private final MutableValueGraph<Integer, RelationshipType> relationships = ValueGraphBuilder.directed().build();

    @Override
    public int save(ClassEntity classNode) {
        int classId = getOrGenerateId(classNode.getName());

        ClassEntity classEntityWithId = classNode.withId(classId);
        data.put(classEntityWithId.getId(), classEntityWithId);
        relationships.addNode(classEntityWithId.getId());

        return classEntityWithId.getId();
    }

    @Override
    public void recordInterfaces(int id, Collection<String> interfaces) {
        for (String interfaceName : interfaces) {
            int interfaceId = getOrGenerateId(interfaceName);
            link(id, interfaceId, RelationshipType.IMPLEMENTS);
        }
    }

    @Override
    public void recordSuperClass(int id, String superClass) {
        int superClassId = getOrGenerateId(superClass);

        if (!data.containsKey(superClassId)) {
            data.put(superClassId, ClassEntity.builder()
                    .id(superClassId)
                    .name(superClass)
                    .packageName("<unknown>")
                    .build());
        }

        link(id, superClassId, RelationshipType.EXTENDS);
    }

    private int getOrGenerateId(String name) {
        int id;
        if (lookup.containsKey(name)) {
            id = lookup.get(name);
        } else {
            id = ID_GENERATOR.getAndIncrement();
            lookup.put(name, id);
        }

        return id;
    }

    private void link(int from, int to, RelationshipType type) {
        relationships.addNode(to);
        relationships.putEdgeValue(from, to, type);
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

    @Override
    public int getSuperClassId(int id) {
        List<Integer> superClasses = filterByType(id, relationships.successors(id), RelationshipType.EXTENDS, false);

        if (superClasses != null && superClasses.size() > 0) {
            return superClasses.get(0);
        }

        return -1;
    }

    @Override
    public Collection<Integer> getInterfaceIds(int id) {
        return filterByType(id, relationships.successors(id), RelationshipType.IMPLEMENTS, false);
    }

    @Override
    public Collection<Integer> getSubclassIds(int id) {
        return filterByType(id, relationships.predecessors(id), RelationshipType.EXTENDS, true);
    }

    @Override
    public Collection<Integer> getImplementorIds(int id) {
        return filterByType(id, relationships.predecessors(id), RelationshipType.IMPLEMENTS, true);
    }

    private List<Integer> filterByType(int fromId, Set<Integer> nodes, RelationshipType expectedType, boolean reverse) {
        return nodes.stream().filter(s -> {
            Optional<RelationshipType> type;
            if (reverse) {
                type = relationships.edgeValue(s, fromId);
            } else {
                type = relationships.edgeValue(fromId, s);
            }
            return type.isPresent() && type.get() == expectedType;
        }).collect(Collectors.toList());
    }

    public enum RelationshipType {
        IMPLEMENTS,
        EXTENDS
    }
}
