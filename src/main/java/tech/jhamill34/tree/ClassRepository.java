package tech.jhamill34.tree;

import tech.jhamill34.entities.ClassEntity;

import java.util.Collection;

public interface ClassRepository {
    int save(ClassEntity classNode);
    void recordInterfaces(int id, Collection<String> interfaces);
    void recordSuperClass(int id, String superClass);

    ClassEntity findById(int id);
    int getId(String name);
    Collection<Integer> allClasses();

    int getSuperClassId(int id);
    Collection<Integer> getInterfaceIds(int id);
    Collection<Integer> getSubclassIds(int id);
    Collection<Integer> getImplementorIds(int id);
}
