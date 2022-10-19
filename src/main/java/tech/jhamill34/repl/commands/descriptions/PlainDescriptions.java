package tech.jhamill34.repl.commands.descriptions;

import org.objectweb.asm.Opcodes;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.entities.*;

public class PlainDescriptions implements EntityVisitor<String>, Opcodes {
    @Override
    public String visitClassEntity(ClassEntity classEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(classEntity.getId()).append('\n');
        sb.append("Class: ").append(EntityUtilities.convertClassName(classEntity.getName())).append('\n');
        sb.append("Package: ").append(classEntity.getPackageName()).append('\n');

        if (classEntity.getSignature() != null) {
            sb.append("Signature: ").append(classEntity.getSignature()).append('\n');
        }

        sb.append("Access: ").append(EntityUtilities.appendAccess(classEntity.getAccess())).append('\n');

        return sb.toString();
    }

    @Override
    public String visitInstructionEntity(InstructionEntity instructionEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(instructionEntity.getId()).append('\n');
        sb.append("Op Code: ").append(EntityUtilities.convertOpcode(instructionEntity.getOpCode())).append('\n');
        sb.append("Line#: ").append(instructionEntity.getLineNumber()).append('\n');
        sb.append("Index: ").append(instructionEntity.getIndex()).append('\n');

        return sb.toString();
    }

    @Override
    public String visitMethodEntity(MethodEntity methodEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(methodEntity.getId()).append('\n');
        sb.append("Name: ").append(methodEntity.getName()).append('\n');

        sb.append("Argument Types: ").append(EntityUtilities.convertArguments(methodEntity)).append('\n');
        sb.append("Return Type: ").append(EntityUtilities.convertReturnType(methodEntity)).append('\n');

        if (methodEntity.getSignature() != null) {
            sb.append("Signature: ").append(methodEntity.getSignature()).append('\n');
        }

        sb.append("Access: ").append(EntityUtilities.appendAccess(methodEntity.getAccess())).append('\n');

        return sb.toString();
    }

    @Override
    public String visitFieldEntity(FieldEntity fieldEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(fieldEntity.getId()).append('\n');
        sb.append("Name: ").append(fieldEntity.getName()).append('\n');

        sb.append("Type: ").append(EntityUtilities.convertFieldType(fieldEntity)).append('\n');

        if (fieldEntity.getSignature() != null) {
            sb.append("Signature: ").append(fieldEntity.getSignature()).append('\n');
        }

        sb.append("Access: ").append(EntityUtilities.appendAccess(fieldEntity.getAccess()));
        return sb.toString();
    }

    @Override
    public String visitValue(IdValue value) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(value.getId()).append('\n');
        sb.append("Type: ").append(value.delegate.getType().getClassName()).append('\n');
        sb.append("Size: ").append(value.delegate.getSize()).append('\n');


        return sb.toString();
    }
}
