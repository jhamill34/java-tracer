package tech.jhamill34.repl.extensions;

import tech.jhamill34.repl.Compiler;

public class CompilerImpl implements Compiler {
    @Override
    public String[] compile(String source) {
        return source.split(System.lineSeparator());
    }
}
