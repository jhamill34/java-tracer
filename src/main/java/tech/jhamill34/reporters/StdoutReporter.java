package tech.jhamill34.reporters;

import tech.jhamill34.reports.Reporter;

public class StdoutReporter implements Reporter {
    @Override
    public void write(String value) {
        System.out.print(value);
    }

    @Override
    public void close() {}
}
