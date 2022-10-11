package tech.jhamill34.repl.commands.descriptions;

import com.google.common.graph.Graph;
import com.google.inject.Inject;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.analyze.IdValue;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.EntityVisitor;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.entities.InstructionEntity;
import tech.jhamill34.entities.MethodEntity;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.FieldRepository;
import tech.jhamill34.tree.InstructionRepository;
import tech.jhamill34.tree.MethodRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class PlainDescriptions implements EntityVisitor<String>, Opcodes {
    @Inject
    private ClassRepository classRepository;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private InstructionRepository instructionRepository;

    @Override
    public String visitClassEntity(ClassEntity classEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(classEntity.getId()).append('\n');
        sb.append("Class: ").append(classEntity.getName().replace('/', '.')).append('\n');
        sb.append("Package: ").append(classEntity.getPackageName()).append('\n');

        if (classEntity.getSignature() != null) {
            sb.append("Signature: ").append(classEntity.getSignature()).append('\n');
        }

        sb.append("Access: ");
        appendAccess(classEntity.getAccess(), sb);
        sb.append('\n');

        return sb.toString();
    }

    @Override
    public String visitInstructionEntity(InstructionEntity instructionEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(instructionEntity.getId()).append('\n');
        sb.append("Op Code: ").append(convertOpcode(instructionEntity.getOpCode())).append('\n');
        sb.append("Line#: ").append(instructionEntity.getLineNumber()).append('\n');
        sb.append("Index: ").append(instructionEntity.getIndex()).append('\n');

        return sb.toString();
    }

    @Override
    public String visitMethodEntity(MethodEntity methodEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(methodEntity.getId()).append('\n');
        sb.append("Name: ").append(methodEntity.getName()).append('\n');

        Type[] arguments = Type.getArgumentTypes(methodEntity.getDescriptor());
        Type returnType = Type.getReturnType(methodEntity.getDescriptor());
        sb.append("Argument Types: \n");
        for (Type arg : arguments) {
            sb.append("  ").append(arg.getClassName()).append('\n');
        }

        sb.append("Return Type: ").append(returnType.getClassName()).append('\n');

        if (methodEntity.getSignature() != null) {
            sb.append("Signature: ").append(methodEntity.getSignature()).append('\n');
        }

        sb.append("Access: ");
        appendAccess(methodEntity.getAccess(), sb);
        sb.append('\n');

        return sb.toString();
    }

    @Override
    public String visitFieldEntity(FieldEntity fieldEntity) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(fieldEntity.getId()).append('\n');
        sb.append("Name: ").append(fieldEntity.getName()).append('\n');

        Type fieldType = Type.getType(fieldEntity.getDescriptor());
        sb.append("Descriptor: ").append(fieldType.getClassName()).append('\n');

        if (fieldEntity.getSignature() != null) {
            sb.append("Signature: ").append(fieldEntity.getSignature()).append('\n');
        }

        sb.append("Access: ");
        appendAccess(fieldEntity.getAccess(), sb);

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

    private void printInstructionSource(StringBuilder sb, int instructionId) {
        InstructionEntity instructionEntity = instructionRepository.findById(instructionId);
        MethodEntity methodEntity = methodRepository.findById(instructionEntity.getInvokerId());
        ClassEntity classEntity = classRepository.findById(methodEntity.getOwnerId());

        sb.append('\t')
                .append(instructionId)
                .append('\t')
                .append(Type.getReturnType(methodEntity.getDescriptor()).getClassName())
                .append(' ')
                .append(classEntity.getName())
                .append('.')
                .append(methodEntity.getName())
                .append('(')
                .append(Arrays.stream(Type.getArgumentTypes(methodEntity.getDescriptor())).map(Type::getClassName).collect(Collectors.joining(", ")))
                .append("):")
                .append(instructionEntity.getLineNumber())
                .append('\n');
    }

    private static void appendAccess(final int accessFlags, StringBuilder stringBuilder) {
        if ((accessFlags & Opcodes.ACC_PUBLIC) != 0) {
            stringBuilder.append("public ");
        }
        if ((accessFlags & Opcodes.ACC_PRIVATE) != 0) {
            stringBuilder.append("private ");
        }
        if ((accessFlags & Opcodes.ACC_PROTECTED) != 0) {
            stringBuilder.append("protected ");
        }
        if ((accessFlags & Opcodes.ACC_FINAL) != 0) {
            stringBuilder.append("final ");
        }
        if ((accessFlags & Opcodes.ACC_STATIC) != 0) {
            stringBuilder.append("static ");
        }
        if ((accessFlags & Opcodes.ACC_SYNCHRONIZED) != 0) {
            stringBuilder.append("synchronized ");
        }
        if ((accessFlags & Opcodes.ACC_VOLATILE) != 0) {
            stringBuilder.append("volatile ");
        }
        if ((accessFlags & Opcodes.ACC_TRANSIENT) != 0) {
            stringBuilder.append("transient ");
        }
        if ((accessFlags & Opcodes.ACC_ABSTRACT) != 0) {
            stringBuilder.append("abstract ");
        }
        if ((accessFlags & Opcodes.ACC_STRICT) != 0) {
            stringBuilder.append("strictfp ");
        }
        if ((accessFlags & Opcodes.ACC_SYNTHETIC) != 0) {
            stringBuilder.append("synthetic ");
        }
        if ((accessFlags & Opcodes.ACC_MANDATED) != 0) {
            stringBuilder.append("mandated ");
        }
        if ((accessFlags & Opcodes.ACC_ENUM) != 0) {
            stringBuilder.append("enum ");
        }
    }

    private static String convertOpcode(int opCode) {
        switch (opCode) {
            case NOP: return "NOP";
            case ACONST_NULL: return "ACONST_NULL";
            case ICONST_M1: return "ICONST_M1";
            case ICONST_0: return "ICONST_0";
            case ICONST_1: return "ICONST_1";
            case ICONST_2: return "ICONST_2";
            case ICONST_3: return "ICONST_3";
            case ICONST_4: return "ICONST_4";
            case ICONST_5: return "ICONST_5";
            case LCONST_0: return "LCONST_0";
            case LCONST_1: return "LCONST_1";
            case FCONST_0: return "FCONST_0";
            case FCONST_1: return "FCONST_1";
            case FCONST_2: return "FCONST_2";
            case DCONST_0: return "DCONST_0";
            case DCONST_1: return "DCONST_1";
            case BIPUSH: return "BIPUSH";
            case SIPUSH: return "SIPUSH";
            case LDC: return "LDC";
            case ILOAD: return "ILOAD";
            case LLOAD: return "LLOAD";
            case FLOAD: return "FLOAD";
            case DLOAD: return "DLOAD";
            case ALOAD: return "ALOAD";
            case IALOAD: return "IALOAD";
            case LALOAD: return "LALOAD";
            case FALOAD: return "FALOAD";
            case DALOAD: return "DALOAD";
            case AALOAD: return "AALOAD";
            case BALOAD: return "BALOAD";
            case CALOAD: return "CALOAD";
            case SALOAD: return "SALOAD";
            case ISTORE: return "ISTORE";
            case LSTORE: return "LSTORE";
            case FSTORE: return "FSTORE";
            case DSTORE: return "DSTORE";
            case ASTORE: return "ASTORE";
            case IASTORE: return "IASTORE";
            case LASTORE: return "LASTORE";
            case FASTORE: return "FASTORE";
            case DASTORE: return "DASTORE";
            case AASTORE: return "AASTORE";
            case BASTORE: return "BASTORE";
            case CASTORE: return "CASTORE";
            case SASTORE: return "SASTORE";
            case POP: return "POP";
            case POP2: return "POP2";
            case DUP: return "DUP";
            case DUP_X1: return "DUP_X1";
            case DUP_X2: return "DUP_X2";
            case DUP2: return "DUP2";
            case DUP2_X1: return "DUP2_X1";
            case DUP2_X2: return "DUP2_X2";
            case SWAP: return "SWAP";
            case IADD: return "IADD";
            case LADD: return "LADD";
            case FADD: return "FADD";
            case DADD: return "DADD";
            case ISUB: return "ISUB";
            case LSUB: return "LSUB";
            case FSUB: return "FSUB";
            case DSUB: return "DSUB";
            case IMUL: return "IMUL";
            case LMUL: return "LMUL";
            case FMUL: return "FMUL";
            case DMUL: return "DMUL";
            case IDIV: return "IDIV";
            case LDIV: return "LDIV";
            case FDIV: return "FDIV";
            case DDIV: return "DDIV";
            case IREM: return "IREM";
            case LREM: return "LREM";
            case FREM: return "FREM";
            case DREM: return "DREM";
            case INEG: return "INEG";
            case LNEG: return "LNEG";
            case FNEG: return "FNEG";
            case DNEG: return "DNEG";
            case ISHL: return "ISHL";
            case LSHL: return "LSHL";
            case ISHR: return "ISHR";
            case LSHR: return "LSHR";
            case IUSHR: return "IUSHR";
            case LUSHR: return "LUSHR";
            case IAND: return "IAND";
            case LAND: return "LAND";
            case IOR: return "IOR";
            case LOR: return "LOR";
            case IXOR: return "IXOR";
            case LXOR: return "LXOR";
            case IINC: return "IINC";
            case I2L: return "I2L";
            case I2F: return "I2F";
            case I2D: return "I2D";
            case L2I: return "L2I";
            case L2F: return "L2F";
            case L2D: return "L2D";
            case F2I: return "F2I";
            case F2L: return "F2L";
            case F2D: return "F2D";
            case D2I: return "D2I";
            case D2L: return "D2L";
            case D2F: return "D2F";
            case I2B: return "I2B";
            case I2C: return "I2C";
            case I2S: return "I2S";
            case LCMP: return "LCMP";
            case FCMPL: return "FCMPL";
            case FCMPG: return "FCMPG";
            case DCMPL: return "DCMPL";
            case DCMPG: return "DCMPG";
            case IFEQ: return "IFEQ";
            case IFNE: return "IFNE";
            case IFLT: return "IFLT";
            case IFGE: return "IFGE";
            case IFGT: return "IFGT";
            case IFLE: return "IFLE";
            case IF_ICMPEQ: return "IF_ICMPEQ";
            case IF_ICMPNE: return "IF_ICMPNE";
            case IF_ICMPLT: return "IF_ICMPLT";
            case IF_ICMPGE: return "IF_ICMPGE";
            case IF_ICMPGT: return "IF_ICMPGT";
            case IF_ICMPLE: return "IF_ICMPLE";
            case IF_ACMPEQ: return "IF_ACMPEQ";
            case IF_ACMPNE: return "IF_ACMPNE";
            case GOTO: return "GOTO";
            case JSR: return "JSR";
            case RET: return "RET";
            case TABLESWITCH: return "TABLESWITCH";
            case LOOKUPSWITCH: return "LOOKUPSWITCH";
            case IRETURN: return "IRETURN";
            case LRETURN: return "LRETURN";
            case FRETURN: return "FRETURN";
            case DRETURN: return "DRETURN";
            case ARETURN: return "ARETURN";
            case RETURN: return "RETURN";
            case GETSTATIC: return "GETSTATIC";
            case PUTSTATIC: return "PUTSTATIC";
            case GETFIELD: return "GETFIELD";
            case PUTFIELD: return "PUTFIELD";
            case INVOKEVIRTUAL: return "INVOKEVIRTUAL";
            case INVOKESPECIAL: return "INVOKESPECIAL";
            case INVOKESTATIC: return "INVOKESTATIC";
            case INVOKEINTERFACE: return "INVOKEINTERFACE";
            case INVOKEDYNAMIC: return "INVOKEDYNAMIC";
            case NEW: return "NEW";
            case NEWARRAY: return "NEWARRAY";
            case ANEWARRAY: return "ANEWARRAY";
            case ARRAYLENGTH: return "ARRAYLENGTH";
            case ATHROW: return "ATHROW";
            case CHECKCAST: return "CHECKCAST";
            case INSTANCEOF: return "INSTANCEOF";
            case MONITORENTER: return "MONITORENTER";
            case MONITOREXIT: return "MONITOREXIT";
            case MULTIANEWARRAY: return "MULTIANEWARRAY";
            case IFNULL: return "IFNULL";
            case IFNONNULL: return "IFNONNULL";
        }
        return "UNKNOWN";
    }
}
