package tech.jhamill34.file;

import org.objectweb.asm.tree.ClassNode;

public interface BytecodeTreeProcessor {
    void process(String name, String parent, ClassNode node);
}
