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
    private ClasspathAnalyzer classpathAnalyzer;

    @Inject
    private Set<Report> reports;

    @Inject
    private REPLHandler replHandler;

    @CommandLine.Option(
            names = {"-cp", "--classpath"},
            description = "The classpath to analylze from a file"
    )
    private String classPathFile;

    @CommandLine.Option(
            names = {"-m", "--mainClass"},
            description = "For analysis this class will be used as the starting point"
    )
    private String mainClass;

    @CommandLine.Option(
            names = {"-s", "--start"},
            description = "First method to call in analysis"
    )
    private String startingMethod;

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

        if (interactive) {
            replHandler.start();
        } else {
            if (mainClass == null || startingMethod == null) {
                System.out.println("If not running in interactive mode, please supply an entry point to create a report");
                return;
            }

            classpathAnalyzer.analyze(mainClass, startingMethod);

            for (Report report : reports) {
                report.report();
            }
        }
    }
}
