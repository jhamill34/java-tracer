package tech.jhamill34.repl.extensions;

import tech.jhamill34.repl.extensions.nodes.Program;

import java.util.List;

public class ASTPipeline {
    public Program execute(String input, boolean isTemplate) throws ParserException, TokenizerException {
        Tokenizer tokenizer = new Tokenizer(input, isTemplate);
        List<Token> tokens = tokenizer.scanTokens();

        Parser parser = new Parser(tokens, isTemplate);
        return parser.parse();
    }
}
