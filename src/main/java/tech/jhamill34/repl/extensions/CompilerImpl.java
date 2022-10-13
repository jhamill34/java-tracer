package tech.jhamill34.repl.extensions;

import com.google.inject.Inject;
import tech.jhamill34.FeatureFlags;
import tech.jhamill34.repl.Compiler;
import tech.jhamill34.repl.extensions.nodes.Program;

import java.util.List;

public class CompilerImpl implements Compiler {
    @Inject
    private FeatureFlags featureFlags;

    @Override
    public String[] compile(String source) {
        if (featureFlags.canUseExtendedCompiler()) {
            Tokenizer tokenizer = new Tokenizer(source);
            CodeGenerator codeGenerator = new CodeGenerator();

            try {
                List<Token> tokens = tokenizer.scanTokens();
                Parser parser = new Parser(tokens);

                Program program = parser.parse();
                program.accept(codeGenerator);
            } catch (TokenizerException | ParserException e) {
                System.out.println(e.getMessage());
            }

            return codeGenerator.getCommands().toArray(new String[0]);
        }

        return source.split(System.lineSeparator());
    }
}
