package tech.jhamill34.repl.extensions;

public enum TokenType {
    // Single character
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, SLASH,
    DOT, MINUS, PLUS, STAR, SEMICOLON, COLON,

    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    VAR, IF, ELSE, WHILE, FUN, RETURN, EXPORT, INCLUDE,

    EOF
}
