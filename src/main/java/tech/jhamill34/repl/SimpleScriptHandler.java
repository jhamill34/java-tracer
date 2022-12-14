package tech.jhamill34.repl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
    public void start(String source, List<String> args, boolean isTemplate) {
        String[] commands = compiler.compile(source, args.size(), isTemplate);

        int current = 0;
        int numConstants = Integer.parseInt(commands[current++]);

        Map<String, Object> constants = stateManager.getConstants();
        for (int i = 0; i < numConstants; i++) {
            String[] constant = commands[current++].split(":", 2);

            char type = constant[1].charAt(0);
            String value = constant[1].substring(1);
            switch (type) {
                case 'I':
                    constants.put(constant[0], Integer.parseInt(value));
                    break;
                case 'B':
                    constants.put(constant[0], value.equalsIgnoreCase("true"));
                    break;
                default:
                    constants.put(constant[0], value);
                    break;
            }
        }

        int numCommands = Integer.parseInt(commands[current++]);
        int ip = current;
        BiMap<String, Integer> labelIndex = HashBiMap.create();
        for (int i = 0; i < numCommands; i++, current++) {
            String line = commands[current];
            if (line.contains(LABEL)) {
                String[] parts = line.split(LABEL);
                labelIndex.put(parts[0], current);
                if (parts.length > 1) {
                    commands[current] = parts[1];
                } else {
                    commands[current] = "";
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
                        reporter.write((value != null ? value.toString() : "null"));
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
            for (int i = 0; i < commands.length; i++) {
                String line = i + ":" + commands[i];

                if (commands[i].isEmpty() && labelIndex.inverse().containsKey(i)) {
                    line += labelIndex.inverse().get(i) + ":";
                }

                if (i == ip) {
                    line += " <--- Error happened here";
                }

                System.err.println(line);
            }
            e.printStackTrace();
        }
    }
}
