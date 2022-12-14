package tech.jhamill34.repl.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private boolean isPlaintext = true;

    private final boolean isTemplate;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("var", TokenType.VAR);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("fun", TokenType.FUN);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("export", TokenType.EXPORT);
        KEYWORDS.put("include", TokenType.INCLUDE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("in", TokenType.IN);
        KEYWORDS.put("null", TokenType.NULL);
    }

    public Tokenizer(String source, boolean isTemplate) {
        this.isTemplate = isTemplate;
        this.source = source;
    }

    public List<Token> scanTokens() throws TokenizerException {
        isPlaintext = peek() != '{' || peekNext() != '{';

        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(Token.builder()
                .type(TokenType.EOF)
                .lexeme("")
                .line(line)
                .build());

        return tokens;
    }

    private void scanToken() throws TokenizerException {
        char c = advance();

        if (isTemplate && isPlaintext) {
            while (!isAtEnd() && (peek() != '{' || peekNext() != '{')) {
                if (peek() == '\n') line++;

                advance();
            }

            String value = source.substring(start, current);
            addToken(TokenType.STRING, value);
            isPlaintext = false;
            return;
        }

        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case ':': addToken(TokenType.COLON); break;
            case '#': addToken(TokenType.POUND); break;
            case '{':
                if (match('{')) {
                    addToken(TokenType.DOUBLE_LEFT_BRACE);
                } else {
                    addToken(TokenType.LEFT_BRACE);
                }
                break;
            case '}':
                if (match('}')) {
                    addToken(TokenType.DOUBLE_RIGHT_BRACE);
                    isPlaintext = true;
                } else {
                    addToken(TokenType.RIGHT_BRACE);
                }
                break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/':
                if (match('/')) {
                    while(peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }

                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw new TokenizerException("Unexpected token " + c + " found on line " + line);
                }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.get(text);

        if (type == null) {
            addToken(TokenType.IDENTIFIER, text);
        } else {
            addToken(type);
        }

    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$';
    }

    private void number() {
        while (isDigit(peek())) advance();

        addToken(TokenType.NUMBER, Integer.parseInt(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() throws TokenizerException {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            throw new TokenizerException("Unterminated String");
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char peek() {
        if (isAtEnd()) return '\0';

        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(Token.builder()
                .type(type)
                .literal(literal)
                .lexeme(text)
                .line(line)
                .build());
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
