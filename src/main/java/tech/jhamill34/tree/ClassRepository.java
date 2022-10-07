package tech.jhamill34.tree;

import tech.jhamill34.entities.ClassEntity;

import java.util.Collection;

public interface ClassRepository {
    int save(ClassEntity classNode);

    ClassEntity findById(int id);
    int getId(String name);
    Collection<Integer> allClasses();
}
