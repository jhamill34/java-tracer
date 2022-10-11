package tech.jhamill34.pico;

import tech.jhamill34.analyze.IdValue;

import java.util.List;

public interface ClasspathAnalyzer {
    boolean analyze(String mainClass, String entryMethod, List<? extends IdValue> args);
}
