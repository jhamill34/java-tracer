package tech.jhamill34.resolvers;

import com.google.inject.Inject;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.FieldRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ClassResolver {
    @Inject
    private ClassRepository classRepository;

    @Inject
    private FieldRepository fieldRepository;

    @Inject
    private MethodRepository methodRepository;

    public ClassEntity getSuperClass(ClassEntity parent) {
        int superClassId = classRepository.getSuperClassId(parent.getId());
        if (superClassId >= 0) {
            return classRepository.findById(superClassId);
        }

        return null;
    }

    public List<ClassEntity> getInterfaces(ClassEntity parent) {
        return classRepository.getInterfaceIds(parent.getId())
                .stream()
                .map(id -> classRepository.findById(id))
                .collect(Collectors.toList());
    }

    public List<ClassEntity> getSubClasses(ClassEntity parent) {
        return classRepository.getSubclassIds(parent.getId())
                .stream()
                .map(id -> classRepository.findById(id))
                .collect(Collectors.toList());
    }

    public List<ClassEntity> getImplementors(ClassEntity parent) {
        return classRepository.getImplementorIds(parent.getId())
                .stream()
                .map(id -> classRepository.findById(id))
                .collect(Collectors.toList());
    }

    public List<FieldEntity> getFields(ClassEntity parent) {
        return fieldRepository.allFieldsForOwner(parent.getId())
                .stream()
                .map(id -> fieldRepository.findById(id))
                .collect(Collectors.toList());
    }

    public List<MethodEntity> getMethods(ClassEntity parent) {
        return methodRepository.allMethodsForOwner(parent.getId())
                .stream()
                .map(id -> methodRepository.findById(id))
                .collect(Collectors.toList());
    }
}
