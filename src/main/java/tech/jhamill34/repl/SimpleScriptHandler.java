package tech.jhamill34.repl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.pico.ScriptHandler;
import tech.jhamill34.reports.Reporter;
import tech.jhamill34.reports.ReporterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SimpleScriptHandler implements ScriptHandler {
    private static final String LABEL = ":";
    private static final String DELIM = "\\s+";

    @Inject
    private Executor executor;

    @Inject
    private ReporterFactory reporterFactory;

    @Inject
    @Named("replstack")
    private Stack<Object> stack;

    @Override
    public void start(String source) {
        String[] commands = source.split(System.lineSeparator());
        int ip = 0;

        Map<String, Integer> labelIndex = new HashMap<>();

        for (int i = 0; i < commands.length; i++) {
            String line = commands[i];
            if (line.contains(LABEL)) {
                String[] parts = line.split(LABEL);
                labelIndex.put(parts[0], i);
                if (parts.length > 1) {
                    commands[i] = parts[1];
                } else {
                    commands[i] = "";
                }
            }
        }

        try (Reporter reporter = reporterFactory.createWithTitle("SCRIPT")) {
            while (ip < commands.length) {
                String line = commands[ip];

                String[] parts = line.trim().split(DELIM);
                String cmd = parts[0].trim();

                List<String> operands = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    operands.add(parts[i].trim());
                }

                switch (cmd) {
                    case "print":
                        reporter.write(stack.pop().toString() + "\n");
                        ip++;
                        break;
                    case "goto":
                        ip = labelIndex.get(operands.get(0));
                        break;
                    case "jmp":
                        Boolean val = (Boolean) stack.pop();
                        if (val) {
                            ip = labelIndex.get(operands.get(0));
                        } else {
                            ip++;
                        }
                        break;
                    default:
                        executor.execute(cmd, operands);
                        ip++;
                }
            }
        } catch (Exception e) {
            System.err.println("Something went wrong...");
        }
    }
}
