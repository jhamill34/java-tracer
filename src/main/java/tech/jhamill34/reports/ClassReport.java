package tech.jhamill34.reports;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.pico.Report;
import tech.jhamill34.tree.ClassRepository;

public class ClassReport implements Report {
    private static final Logger logger = LoggerFactory.getLogger(ClassReport.class);

    @Inject
    private ReporterFactory reporterFactory;

    @Inject
    private ClassRepository classRepository;

    @Override
    public void report() {
        try (Reporter reporter = reporterFactory.createWithTitle("class_report")) {
            for (int classId : classRepository.allClasses()) {
                reporter.write("" + classId);
            }
        } catch (Exception e) {
            logger.error("Failed to get a reporter", e);
        }
    }
}
