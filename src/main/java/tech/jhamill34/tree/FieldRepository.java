package tech.jhamill34.tree;

import tech.jhamill34.entities.FieldEntity;

import java.util.Collection;

public interface FieldRepository {
    int save(FieldEntity fieldEntity);
    int getId(int ownerId, String name);
    FieldEntity findById(int id);
    Collection<Integer> allFields();
    Collection<Integer> allFieldsForOwner(int ownerId);
}
