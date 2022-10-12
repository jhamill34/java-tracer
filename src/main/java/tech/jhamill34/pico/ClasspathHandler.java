package tech.jhamill34.pico;

import com.google.common.io.Files;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import tech.jhamill34.app.CLIHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@CommandLine.Command(
        name = "classpath",
        description = "Processes/Analyzes everything in a provided classpath"
)
public class ClasspathHandler implements CLIHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClasspathHandler.class);

    @Inject
    private PathProcessor pathProcessor;

    @Inject
    private REPLHandler replHandler;

    @Inject
    private ScriptHandler scriptHandler;

    @CommandLine.Option(
            names = {"-cp", "--classpath"},
            description = "The classpath to analylze from a file"
    )
    private String classPathFile;

    @CommandLine.Option(
            names = {"-i", "--interactive"},
            description = "run in interactive mode"
    )
    private boolean interactive;

    @CommandLine.Option(
            names = {"-d", "--delimiter"},
            description = "Delimiter for classpath",
            defaultValue = ":"
    )
    private String classPathDelimiter;

    @CommandLine.Option(
            names = {"-x", "--script"},
            description = "Script to execute"
    )
    private String scriptFile;

    @CommandLine.Option(
            names = {"-a", "--arg"},
            description = "Arguments to pass to the scriptFile specified"
    )
    private List<String> scriptArgs;

    @Override
    public void run() {
        if (classPathFile != null) {
            System.out.println("Processing...");

            String classPath;
            try {
                classPath = Files.asCharSource(new File(classPathFile), StandardCharsets.UTF_8).read().trim();
            } catch (IOException e) {
                logger.error("Failed to read classpath file", e);
                return;
            }

            String[] parts = classPath.split(classPathDelimiter);
            logger.info("Processing " + parts.length + " items in classpath");

            for (String path : parts) {
                pathProcessor.process(path);
            }

            System.out.println("Done!");
        }

        if (scriptFile != null) {
            List<String> args = Collections.emptyList();
            if (scriptArgs != null) {
                args = scriptArgs;
            }

            try {
                String scriptContents = Files.asCharSource(new File(scriptFile), StandardCharsets.UTF_8).read();
                scriptHandler.start(scriptContents, args);
            } catch (IOException e) {
                logger.error("Failed to read script", e);
            }
        }

        if (interactive) {
            replHandler.start();
        }
    }
}
