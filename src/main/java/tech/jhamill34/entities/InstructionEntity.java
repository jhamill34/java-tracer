package tech.jhamill34.entities;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@Value
public class InstructionEntity implements Entity {
    @With int id;
    int invokerId;

    int opCode;
    int lineNumber;
    int index;

    @Override
    public String accept(EntityVisitor entityVisitor) {
        return entityVisitor.visitInstructionEntity(this);
    }
}
