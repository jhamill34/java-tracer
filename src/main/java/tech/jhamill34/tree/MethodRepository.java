package tech.jhamill34.tree;

import tech.jhamill34.entities.MethodEntity;

import java.util.Collection;

public interface MethodRepository {
    int save(MethodEntity methodNode);
    int getId(int ownerId, String name);
    MethodEntity findById(int id);
    Collection<Integer> allMethods();
    Collection<Integer> allMethodsForOwner(int ownerId);
}
