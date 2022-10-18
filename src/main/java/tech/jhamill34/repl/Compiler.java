package tech.jhamill34.repl;

public interface Compiler {
    String[] compile(String source, int argc, boolean isTemplate);
}
