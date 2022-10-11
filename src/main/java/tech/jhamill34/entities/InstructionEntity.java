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
    int referenceId;
    ReferenceType referenceType;

    @Override
    public String accept(EntityVisitor<String> entityVisitor) {
        return entityVisitor.visitInstructionEntity(this);
    }


    public enum ReferenceType {
        METHOD,
        FIELD
    }
}
