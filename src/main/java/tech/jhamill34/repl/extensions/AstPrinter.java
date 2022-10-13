package tech.jhamill34.repl.extensions;

import tech.jhamill34.repl.extensions.nodes.Expression;
import tech.jhamill34.repl.extensions.nodes.Statement;

import java.util.Map;
import java.util.stream.Collectors;

public class AstPrinter implements Expression.Visitor<String>, Statement.Visitor<String> {
    @Override
    public String visitBinary(Expression.Binary expr) {
        return parenthesize(expr.getOperator().getLexeme(), expr.getLeft(), expr.getRight());
    }

    @Override
    public String visitUnary(Expression.Unary expr) {
        return parenthesize(expr.getOperator().getLexeme(), expr.getExpression());
    }

    @Override
    public String visitLiteral(Expression.Literal expr) {
        if (expr.getValue() == null) return "nil";
        return expr.getValue().toString();
    }

    @Override
    public String visitIdentifier(Expression.Identifier expr) {
        return "id:" + expr.getValue().getLexeme();
    }

    @Override
    public String visitGrouping(Expression.Grouping expr) {
        return parenthesize("group", expr.getGroup());
    }

    @Override
    public String visitCall(Expression.Call expr) {
        return parenthesize("call " + expr.getCallee().accept(this), expr.getArguments().toArray(new Expression[0]));
    }

    @Override
    public String visitAssign(Expression.Assign expr) {
        return parenthesize("assign " + expr.getName().getLexeme(), expr.getValue());
    }

    @Override
    public String visitGet(Expression.Get expr) {
        return parenthesize("get " + expr.getName().getLexeme(), expr.getObject());
    }

    @Override
    public String visitExprStatement(Statement.ExprStatement stmt) {
        return stmt.getExpression().accept(this);
    }

    @Override
    public String visitVar(Statement.Var stmt) {
        return parenthesize("declare " + stmt.getName().getLexeme(), stmt.getInitializer());
    }

    @Override
    public String visitBlock(Statement.Block block) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');

        for (Statement stmt : block.getStatements()) {
            sb.append('\t').append(stmt.accept(this)).append('\n');
        }

        return sb.toString();
    }

    @Override
    public String visitWhile(Statement.While stmt) {
        StringBuilder sb = new StringBuilder();

        sb.append(parenthesize("while", stmt.getCondition()));
        sb.append(stmt.getBody().accept(this));

        return sb.toString();
    }

    @Override
    public String visitIf(Statement.If stmt) {
        StringBuilder sb = new StringBuilder();

        sb.append(parenthesize("if", stmt.getCondition()));
        sb.append(stmt.getThenBranch().accept(this));

        if (stmt.getElseBranch() != null) {
            sb.append(stmt.getElseBranch().accept(this));
        }

        return sb.toString();
    }

    @Override
    public String visitFunction(Statement.FunctionDecl stmt) {
        StringBuilder sb = new StringBuilder();

        sb.append("decl (").append(stmt.getName().getLexeme()).append(" ").append(stmt.getParams().stream().map(Token::getLexeme).collect(Collectors.joining(" "))).append(")").append('\n');

        for (Statement s : stmt.getBody()) {
            sb.append('\t').append(s.accept(this)).append('\n');
        }

        return sb.toString();
    }

    @Override
    public String visitReturn(Statement.Return stmt) {
        if (stmt.getValue() == null) {
            return "(ret)";
        }

        return parenthesize("ret", stmt.getValue());
    }

    @Override
    public String visitExport(Statement.Export stmt) {
        StringBuilder sb = new StringBuilder();
        sb.append("(export");

        for (Token token : stmt.getTokens()) {
            sb.append(' ').append(token.getLexeme());
        }

        for (Map.Entry<Token, Expression> entry : stmt.getComputedTokens().entrySet()) {
            sb.append(' ').append(parenthesize("compute " + entry.getKey().getLexeme(), entry.getValue()));
        }

        sb.append(")");
        return sb.toString();
    }

    private String parenthesize(String name, Expression ...exprs) {
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        sb.append(name);

        for (Expression expr : exprs) {
            sb.append(' ').append(expr.accept(this));
        }

        sb.append(')');

        return sb.toString();
    }
}
