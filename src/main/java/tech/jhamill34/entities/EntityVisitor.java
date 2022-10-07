package tech.jhamill34.entities;

import tech.jhamill34.analyze.IdValue;

public interface EntityVisitor {
    String visitClassEntity(ClassEntity classEntity);
    String visitInstructionEntity(InstructionEntity instructionEntity);
    String visitMethodEntity(MethodEntity methodEntity);
    String visitFieldEntity(FieldEntity fieldEntity);
    String visitValue(IdValue value);
}
