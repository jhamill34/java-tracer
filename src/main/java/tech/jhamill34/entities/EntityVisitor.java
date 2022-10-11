package tech.jhamill34.entities;

import tech.jhamill34.analyze.IdValue;

public interface EntityVisitor<T> {
    T visitClassEntity(ClassEntity classEntity);
    T visitInstructionEntity(InstructionEntity instructionEntity);
    T visitMethodEntity(MethodEntity methodEntity);
    T visitFieldEntity(FieldEntity fieldEntity);
    T visitValue(IdValue value);
}
