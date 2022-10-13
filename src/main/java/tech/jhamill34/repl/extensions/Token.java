package tech.jhamill34.repl.extensions;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Token {
    TokenType type;
    String lexeme;
    Object literal;
    int line;
}
