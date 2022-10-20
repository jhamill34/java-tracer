package tech.jhamill34.repl.commands.attributes;

import com.google.inject.Inject;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.resolvers.*;

public class QueryVisitor implements EntityVisitor<Query> {
    @Inject
    private ClassResolver classResolver;

    @Inject
    private FieldResolver fieldResolver;

    @Inject
    private MethodResolver methodResolver;

    @Inject
    private ValueResolver valueResolver;

    @Inject
    private InstructionResolver instructionResolver;

    @Override
    public Query visitClassEntity(ClassEntity classEntity) {
        return ObjectQuery.of(classEntity, classResolver);
    }

    @Override
    public Query visitInstructionEntity(InstructionEntity instructionEntity) {
        return ObjectQuery.of(instructionEntity, instructionResolver);
    }

    @Override
    public Query visitMethodEntity(MethodEntity methodEntity) {
        return ObjectQuery.of(methodEntity, methodResolver);
    }

    @Override
    public Query visitFieldEntity(FieldEntity fieldEntity) {
        return ObjectQuery.of(fieldEntity, fieldResolver);
    }

    @Override
    public Query visitValue(IdValue value) {
        return ObjectQuery.of(value, valueResolver);
    }
}
