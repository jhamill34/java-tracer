package tech.jhamill34.repl;

import com.google.inject.Inject;
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
    private StateManager stateManager;

    @Inject
    private Compiler compiler;

    @Override
    public void start(String source, List<String> args) {
        String[] commands = compiler.compile(source, args.size());
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

                Stack<Object> stack = stateManager.getStack();
                switch (cmd) {
                    case "print":
                        Object value = stack.pop();
                        reporter.write((value != null ? value.toString() : "null") + "\n");
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
                    case "call":
                        int argc = Integer.parseInt(operands.get(0));
                        String label = stateManager.push(ip + 1, argc);
                        ip = labelIndex.get(label);
                        break;
                    case "return":
                        ip = stateManager.pop();
                        break;
                    default:
                        executor.execute(cmd, operands);
                        ip++;
                }
            }
        } catch (Exception e) {
            System.err.println("Something went wrong...");
            e.printStackTrace();
        }
    }
}
