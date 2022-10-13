package tech.jhamill34.repl.extensions;

import com.google.inject.Inject;
import tech.jhamill34.FeatureFlags;
import tech.jhamill34.repl.Compiler;
import tech.jhamill34.repl.extensions.nodes.Statement;

import java.util.List;

public class CompilerImpl implements Compiler {
    @Inject
    private FeatureFlags featureFlags;

    @Override
    public String[] compile(String source) {
        if (featureFlags.canUseExtendedCompiler()) {
            Tokenizer tokenizer = new Tokenizer(source);

            try {
                List<Token> tokens = tokenizer.scanTokens();
                Parser parser = new Parser(tokens);

                List<Statement> statements = parser.parse();

                for (Statement stmt : statements) {
                    System.out.println(stmt.accept(new AstPrinter()));
                }
            } catch (TokenizerException | ParserException e) {
                System.out.println(e.getMessage());
            }

            return new String[0];
        }

        return source.split(System.lineSeparator());
    }
}
