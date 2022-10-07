package tech.jhamill34.file;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.path.FileProcessor;

import java.io.IOException;
import java.io.InputStream;

public class BytecodeProcessor implements FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BytecodeProcessor.class);

    @Inject
    private BytecodeTreeProcessor treeProcessor;

    @Override
    public void process(String name, String parent, InputStream inputStream) {
        byte[] bytes;
        try {
            bytes = ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            logger.error("Failure to read " + name + " in " + parent, e);
            return;
        }

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();

        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        treeProcessor.process(name, parent, classNode);
    }
}
