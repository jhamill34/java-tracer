package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import tech.jhamill34.pico.Report;
import tech.jhamill34.reporters.StdoutReporterFactory;
import tech.jhamill34.reports.ClassReport;
import tech.jhamill34.reports.ReporterFactory;
import tech.jhamill34.reports.ValueReport;

public class ReportingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ReporterFactory.class).to(StdoutReporterFactory.class).in(Singleton.class);

        Multibinder<Report> multibinder = Multibinder.newSetBinder(binder(), Report.class);
        // multibinder.addBinding().to(ClassReport.class).in(Singleton.class);
        multibinder.addBinding().to(ValueReport.class).in(Singleton.class);
    }
}
