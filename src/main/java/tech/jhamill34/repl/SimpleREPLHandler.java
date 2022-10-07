package tech.jhamill34.repl;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import tech.jhamill34.pico.REPLHandler;

import java.util.Scanner;

public class SimpleREPLHandler implements REPLHandler {
    @Inject
    private Executor executor;

    @Override
    public void start() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        System.out.print("> ");
        while(running && scanner.hasNextLine()) {
            // Read
            String line = scanner.nextLine().trim();

            // Execute
            String result = executor.execute(line);

                // Print
            if (line.equalsIgnoreCase("bye")) {
                running = false;
            } else {
                System.out.println(result);
                // Loop
                System.out.print("> ");
            }
        }

        System.out.println("Good bye!");
    }
}
