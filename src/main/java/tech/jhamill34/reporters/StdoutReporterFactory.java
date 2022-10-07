package tech.jhamill34.reporters;

import tech.jhamill34.reports.Reporter;
import tech.jhamill34.reports.ReporterFactory;

public class StdoutReporterFactory implements ReporterFactory {
    @Override
    public Reporter createWithTitle(String title) {
        return new StdoutReporter();
    }
}
