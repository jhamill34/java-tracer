package tech.jhamill34.entities;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@Value
public class MethodEntity implements Entity {
    @With int id;
    int ownerId;

    int access;
    String name;
    String descriptor;
    String signature;

    @Override
    public <T> T accept(EntityVisitor<T> entityVisitor) {
        return entityVisitor.visitMethodEntity(this);
    }
}
