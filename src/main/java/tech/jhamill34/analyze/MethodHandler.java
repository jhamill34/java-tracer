package tech.jhamill34.analyze;

import org.objectweb.asm.tree.MethodInsnNode;
import tech.jhamill34.analyze.IdValue;

import java.util.List;

public interface MethodHandler {
    IdValue invoke(MethodInsnNode insn, List<? extends IdValue> args, IdValue defaultValue);
}
