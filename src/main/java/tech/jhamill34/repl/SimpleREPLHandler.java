package tech.jhamill34.repl;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import tech.jhamill34.pico.REPLHandler;
import tech.jhamill34.reports.Reporter;
import tech.jhamill34.reports.ReporterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleREPLHandler implements REPLHandler {
    private static final String DELIM = "\\s+";

    @Inject
    private Executor executor;

    @Inject
    private ReporterFactory reporterFactory;

    @Override
    public void start() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        try (Reporter reporter = reporterFactory.createWithTitle("REPL")) {
            System.out.print("> ");
            while(running && scanner.hasNextLine()) {
                // Read
                String line = scanner.nextLine().trim();

                String[] parts = line.trim().split(DELIM);
                String cmd = parts[0].trim();

                List<String> operands = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    operands.add(parts[i].trim());
                }

                // Execute
                String result = executor.execute(cmd, operands) + '\n';

                // Print
                if (line.equalsIgnoreCase("bye")) {
                    running = false;
                } else {
                    reporter.write(result);
                    // Loop
                    reporter.write("> ");
                }
            }

            reporter.write("Good bye!\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Something went wrong...");
        }
    }
}
