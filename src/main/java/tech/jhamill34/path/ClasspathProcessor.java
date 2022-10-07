package tech.jhamill34.path;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.pico.PathProcessor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClasspathProcessor implements PathProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ClasspathProcessor.class);
    private static final String CLASS_SUFFIX = ".class";

    @Inject
    private FileProcessor fileProcessor;

    @Override
    public void process(String path) {
        try (JarFile jarFile = new JarFile(path)) {
            List<JarEntry> entries = Collections.list(jarFile.entries())
                    .stream()
                    .filter(e -> e.getName().endsWith(CLASS_SUFFIX))
                    .collect(Collectors.toList());

            String[] jarFileParts = jarFile.getName().split("/");
            String packageName = jarFileParts[jarFileParts.length - 1].replace(".jar", "");

            logger.info("Processing " + entries.size() + " classes found in " + packageName);
            for (JarEntry jarEntry : entries) {
                fileProcessor.process(jarEntry.getName(), packageName, jarFile.getInputStream(jarEntry));
            }
        } catch (IOException e) {
            // We need to log this error somehow
            logger.error("Failure handling JarFile " + path, e);
        }
    }
}
