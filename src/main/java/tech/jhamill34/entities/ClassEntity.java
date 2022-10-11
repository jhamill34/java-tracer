package tech.jhamill34.entities;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@Value
public class ClassEntity implements Entity {
    @With int id;

    int access;
    String name;
    String signature;
    String packageName;

    @Override
    public <T> T accept(EntityVisitor<T> entityVisitor) {
        return entityVisitor.visitClassEntity(this);
    }
}
