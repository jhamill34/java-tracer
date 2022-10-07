package tech.jhamill34.reports;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.pico.Report;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.InstructionRepository;
import tech.jhamill34.tree.MethodRepository;

public class ValueReport implements Report {
    private static final Logger logger = LoggerFactory.getLogger(ValueReport.class);

    @Inject
    private ReporterFactory reporterFactory;

    @Inject
    private HeapStore heapStore;

    @Inject
    private MethodRepository methodRepository;

    @Inject
    private ClassRepository classRepository;

    @Inject
    private InstructionRepository instructionRepository;

    @Override
    public void report() {
        try (Reporter reporter = reporterFactory.createWithTitle("value_report")) {
            for (int valueId : heapStore.allValues()) {
                reporter.write(String.valueOf(valueId));
            }
        } catch (Exception e) {
            logger.error("Failure to get reporter", e);
        }
    }
}
