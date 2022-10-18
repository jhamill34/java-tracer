package tech.jhamill34.repl.extensions;

import tech.jhamill34.repl.extensions.nodes.Expression;
import tech.jhamill34.repl.extensions.nodes.Program;
import tech.jhamill34.repl.extensions.nodes.Statement;
import tech.jhamill34.repl.extensions.nodes.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final List<Token> tokens;
    private final boolean isTemplate;
    private int current = 0;

    public Parser(List<Token> tokens, boolean isTemplate) {
        this.tokens = tokens;
        this.isTemplate = isTemplate;
    }

    public Program parse() throws ParserException {
        List<Statement> includes = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Statement> functionDeclarations = new ArrayList<>();
        Statement exportStatement = Statement.NoOp.of();

        if (isTemplate && !isAtEnd()) {
            statements.add(templateStatement(false));
        }

        while(!isAtEnd()) {
            if (match(TokenType.FUN)) {
                functionDeclarations.add(functionDeclaration());
            } else if (match(TokenType.EXPORT)) {
                if (exportStatement instanceof Statement.NoOp) {
                    exportStatement = exportStatement();
                } else {
                    throw error(previous(), "Only one export statement allowed");
                }
            } else if (match(TokenType.INCLUDE)) {
                includes.add(includeStatement());
            } else {
                statements.add(declaration());
            }
        }

        return Program.of(statements, includes, functionDeclarations, exportStatement);
    }

    private Statement includeStatement() throws ParserException {
        Token include = consume(TokenType.STRING, "Expected a string for including");
        consume(TokenType.SEMICOLON, "Expected ';' after include statement.");

        return Statement.Include.of(previous(), include);
    }

    private Statement declaration() throws ParserException {
            if (match(TokenType.VAR)) return varDeclaration();

            return statement();
    }

    private Statement statement() throws ParserException {
        if (match(TokenType.LEFT_BRACE)) return Statement.Block.of(block());
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.FOR)) return forEachStatement();
        if (match(TokenType.RETURN)) return returnStatement();

        if (isTemplate && match(TokenType.DOUBLE_RIGHT_BRACE)) return templateStatement(false);
        if (isTemplate && match(TokenType.MINUS) && match(TokenType.DOUBLE_RIGHT_BRACE)) return templateStatement(true);

        return expressionStatement();
    }

    private Statement templateStatement(boolean trim) throws ParserException {
        Template template;
        if (check(TokenType.STRING)) {
            template = plainTextStatement(trim);
        } else {
            consume(TokenType.DOUBLE_LEFT_BRACE, "Expected '{{' before printable expression.");

            if (!match(TokenType.POUND)) {
                template = printableExpression();
            } else {
                return Statement.NoOp.of();
            }
        }

        return Statement.TemplateStatement.of(template);
    }

    private Template plainTextStatement(boolean trim) throws ParserException {
        if (isAtEnd()) {
            return Template.Empty.of();
        }

        Token token = consume(TokenType.STRING, "Expected plain text after '}}'.");

        String text = token.getLiteral().toString();
        if (trim && text.charAt(0) == '\n') {
            text = text.substring(1);
        }

        Template next = Template.Empty.of();
        if (!isAtEnd()) {
            consume(TokenType.DOUBLE_LEFT_BRACE, "Expected '{{' after template text.");

            if (!match(TokenType.POUND)) {
                next = printableExpression();
            }
        }

        return Template.PlainText.of(text, next);
    }

    private Template printableExpression() throws ParserException {
        Expression expression = expression();

        boolean trim = match(TokenType.MINUS);
        consume(TokenType.DOUBLE_RIGHT_BRACE, "Expected closing '}}' after printable expression");

        Template next = Template.Empty.of();

        if (check(TokenType.STRING)) {
            next = plainTextStatement(trim);
        }

        return Template.PrintableExpression.of(expression, next);
    }

    private Statement forEachStatement() throws ParserException {
        consume(TokenType.LEFT_PAREN, "Expect '(' after for.");
        consume(TokenType.VAR, "Expect item declaration with 'var' at start of for-each loop.");

        Token item = consume(TokenType.IDENTIFIER, "Expect identifier declaration.");
        consume(TokenType.IN, "Expect 'in' after item declaration.");

        Expression iterable = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after iterable in for-each loop.");

        Statement body = statement();

        return Statement.ForEach.of(item, iterable, body);
    }

    private Statement functionDeclaration() throws ParserException {
        Token name = consume(TokenType.IDENTIFIER, "Expected function name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    throw error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while(match(TokenType.COMMA));
        }

        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");

        consume(TokenType.LEFT_BRACE, "Expect '{' before function body.");
        List<Statement> body = block();
        return Statement.FunctionDecl.of(name, parameters, body);
    }

    private Statement exportStatement() throws ParserException {
        consume(TokenType.LEFT_BRACE, "Expect '{' before exporting.");

        List<Token> tokens = new ArrayList<>();
        Map<Token, Expression> computedTokens = new HashMap<>();
        if (!check(TokenType.RIGHT_BRACE)) {
            do {
                Token token = consume(TokenType.IDENTIFIER, "Expect exported name.");

                if (match(TokenType.COLON)) {
                    Expression expr = expression();
                    computedTokens.put(token, expr);
                } else {
                    tokens.add(token);
                }
            } while(match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after exports.");

        return Statement.Export.of(tokens, computedTokens);
    }

    private Statement returnStatement() throws ParserException {
        Token keyword = previous();
        Expression value = Expression.NoOp.of();
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }

        consume(TokenType.SEMICOLON, "Expected ';' after return value.");
        return Statement.Return.of(keyword, value);
    }

    private List<Statement> block() throws ParserException {
        List<Statement> statements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Statement ifStatement() throws ParserException {
        consume(TokenType.LEFT_PAREN, "Expect '(' after if.");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

        Statement thenBranch = statement();
        Statement elseBranch = Statement.NoOp.of();
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return Statement.If.of(condition, thenBranch, elseBranch);
    }

    private Statement whileStatement() throws ParserException {
        consume(TokenType.LEFT_PAREN, "Expect '(' after while.");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after while condition.");

        Statement body = statement();

        return Statement.While.of(condition, body);
    }

    private Statement varDeclaration() throws ParserException {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");

        Expression initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return Statement.Var.of(name, initializer);
    }

    private Statement expressionStatement() throws ParserException {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");

        return Statement.ExprStatement.of(expr);
    }

    private Expression expression() throws ParserException {
        return assignment();
    }

    private Expression assignment() throws ParserException {
        Expression expr = equality();

        if (match(TokenType.EQUAL)) {
            Token equals = previous();

            Expression value = assignment();

            if (expr instanceof Expression.Identifier) {
                Token token = ((Expression.Identifier) expr).getValue();
                return Expression.Assign.of(token, value);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expression equality() throws ParserException {
        Expression expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();

            expr = Expression.Binary.of(expr, operator, right);
        }

        return expr;
    }

    private Expression comparison() throws ParserException {
        Expression expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expr = Expression.Binary.of(expr, operator, right);
        }

        return expr;
    }

    private Expression term() throws ParserException {
        Expression expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression right = factor();

            expr = Expression.Binary.of(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() throws ParserException {
        Expression expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = unary();

            expr = Expression.Binary.of(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() throws ParserException {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return Expression.Unary.of(operator, right);
        }

        return call();
    }

    private Expression call() throws ParserException {
        Expression expr = primary();

        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(TokenType.DOT)) {
                Token name = consume(TokenType.IDENTIFIER, "Expect propery name after '.'.");
                expr = Expression.Get.of(expr, name);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expression finishCall(Expression callee) throws ParserException {
        List<Expression> args = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                args.add(expression());
            } while (match(TokenType.COMMA)) ;
        }

        Token paren = consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments.");

        return Expression.Call.of(callee, paren, args);
    }

    private Expression primary() throws ParserException {
        if (match(TokenType.NULL)) {
            return Expression.Literal.of(null);
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expression.Identifier.of(previous());
        }

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expression.Literal.of(previous().getLiteral());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");

            return Expression.Grouping.of(expression);
        }

        throw error(peek(), "Expected expression.");
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;

            switch (peek().getType()) {
                case FUN:
                case VAR:
                case WHILE:
                    return;
            }

            advance();
        }
    }

    private Token consume(TokenType type, String message) throws ParserException {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParserException error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            return new ParserException("line " + token.getLine() + " at end: " + message);
        } else {
            return new ParserException("line " + token.getLine() + " at " + token.getLexeme() + ": " + message);
        }
    }

    private boolean match(TokenType ...types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
