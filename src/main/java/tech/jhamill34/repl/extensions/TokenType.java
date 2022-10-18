package tech.jhamill34.repl.extensions;

public enum TokenType {
    // Single character
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, SLASH,
    DOT, MINUS, PLUS, STAR, SEMICOLON, COLON, POUND,

    DOUBLE_LEFT_BRACE,
    DOUBLE_RIGHT_BRACE,

    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    VAR, IF, ELSE, WHILE, FUN, RETURN, EXPORT, INCLUDE, FOR, IN,
    NULL,

    EOF
}
