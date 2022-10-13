package tech.jhamill34.repl.extensions;

import com.google.inject.Inject;
import tech.jhamill34.FeatureFlags;
import tech.jhamill34.repl.Compiler;
import tech.jhamill34.repl.extensions.nodes.Program;

public class CompilerImpl implements Compiler {
    @Inject
    private FeatureFlags featureFlags;

    @Inject
    private ASTPipeline pipeline;

    @Override
    public String[] compile(String source, int argc) {
        if (featureFlags.canUseExtendedCompiler()) {
            CodeGenerator codeGenerator = new CodeGenerator(pipeline, argc);

            try {
                Program program = pipeline.execute(source);
                program.accept(codeGenerator);
            } catch (TokenizerException | ParserException e) {
                System.out.println(e.getMessage());
            }

            return codeGenerator.getCommands().toArray(new String[0]);
        }

        return source.split(System.lineSeparator());
    }
}
