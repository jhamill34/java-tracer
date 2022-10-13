package tech.jhamill34.repl.extensions;

import tech.jhamill34.repl.extensions.nodes.Expression;
import tech.jhamill34.repl.extensions.nodes.Program;
import tech.jhamill34.repl.extensions.nodes.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeGenerator implements Program.Visitor<Void>, Statement.Visitor<Void>, Expression.Visitor<Void> {
    private static int labelId = 0;
    private Environment currentEnv = null;
    private final Set<String> functions = new HashSet<>();
    private final List<String> commands = new ArrayList<>();

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public Void visitBinary(Expression.Binary expr) {
        expr.getLeft().accept(this);
        expr.getRight().accept(this);

        switch (expr.getOperator().getType()) {
            case PLUS:
                commands.add("math +");
                break;
            case MINUS:
                commands.add("math -");
                break;
            case STAR:
                commands.add("math *");
                break;
            case SLASH:
                commands.add("math /");
                break;
            case LESS:
                commands.add("cmp <");
                break;
            case LESS_EQUAL:
                commands.add("cmp <=");
                break;
            case GREATER:
                commands.add("cmp >");
                break;
            case GREATER_EQUAL:
                commands.add("cmp >=");
                break;
            case BANG_EQUAL:
                commands.add("cmp !");
                break;
            case EQUAL_EQUAL:
                commands.add("cmp =");
                break;
        }
        return null;
    }

    @Override
    public Void visitUnary(Expression.Unary expr) {
        expr.getExpression().accept(this);

        switch (expr.getOperator().getType()) {
            case PLUS:
                break;
            case MINUS:
                commands.add("push -1");
                commands.add("math *");
                break;
        }

        return null;
    }

    @Override
    public Void visitLiteral(Expression.Literal expr) {
        commands.add("push " + expr.getValue());
        return null;
    }

    @Override
    public Void visitIdentifier(Expression.Identifier expr) {
        String id = expr.getValue().getLexeme();

        if (functions.contains(id)) {
            commands.add("push " + id);
        } else {
            commands.add("load " + currentEnv.find(expr.getValue().getLexeme()));
        }
        return null;
    }

    @Override
    public Void visitGrouping(Expression.Grouping expr) {
        expr.getGroup().accept(this);
        return null;
    }

    @Override
    public Void visitCall(Expression.Call expr) {
        expr.getCallee().accept(this);
        for (Expression arg : expr.getArguments()) {
            arg.accept(this);
        }
        commands.add("call " + expr.getArguments().size());
        return null;
    }

    @Override
    public Void visitAssign(Expression.Assign expr) {
        expr.getValue().accept(this);
        commands.add("store " + currentEnv.find(expr.getName().getLexeme()));
        return null;
    }

    @Override
    public Void visitGet(Expression.Get expr) {
        expr.getObject().accept(this);
        commands.add("attr " + expr.getName().getLexeme());
        return null;
    }

    @Override
    public Void visitProgram(Program program) {
        Environment root = Environment.of(null);
        commands.add("goto main");

        // Add built in methods here
        executeBlock(program.getFunctionDeclarations(), root);

        commands.add("main:");
        // Execute block without function declarations or export (hoist functions)
        executeBlock(program.getStatements(), root);

        // Execute export
        return null;
    }

    @Override
    public Void visitExprStatement(Statement.ExprStatement stmt) {
        return stmt.getExpression().accept(this);
    }

    @Override
    public Void visitVar(Statement.Var stmt) {
        currentEnv.declare(stmt.getName().getLexeme());

        if (stmt.getInitializer() != null) {
            stmt.getInitializer().accept(this);
            commands.add("store " + currentEnv.find(stmt.getName().getLexeme()));
        }

        return null;
    }

    @Override
    public Void visitBlock(Statement.Block block) {
        executeBlock(block.getStatements(), Environment.of(currentEnv));
        return null;
    }

    @Override
    public Void visitIf(Statement.If stmt) {
        int currentLabel = labelId++;
        stmt.getCondition().accept(this);
        commands.add("jmp if" + currentLabel);
        commands.add("goto else" + currentLabel);
        commands.add("if" + currentLabel + ":");
        stmt.getThenBranch().accept(this);
        commands.add("goto end" + currentLabel);
        commands.add("else" + currentLabel + ":");
        if (stmt.getElseBranch() != null) {
            stmt.getElseBranch().accept(this);
        }
        commands.add("end" + currentLabel + ":");
        return null;
    }

    @Override
    public Void visitWhile(Statement.While stmt) {
        int currentLabel = labelId++;
        commands.add("loop" + currentLabel + ":");
        stmt.getCondition().accept(this);
        commands.add("jmp body" + currentLabel);
        commands.add("goto end" + currentLabel);
        commands.add("body" + currentLabel + ":");
        stmt.getBody().accept(this);
        commands.add("goto loop" + currentLabel);
        commands.add("end" + currentLabel + ":");
        return null;
    }

    @Override
    public Void visitFunction(Statement.FunctionDecl stmt) {
        commands.add(stmt.getName().getLexeme() + ":");
        functions.add(stmt.getName().getLexeme());

        Environment funcEnv = Environment.of(null);
        for (int i = stmt.getParams().size() - 1; i >= 0; i--) {
            Token param = stmt.getParams().get(i);
            funcEnv.declare(param.getLexeme());
            commands.add("store " + funcEnv.find(param.getLexeme()));
        }

        executeBlock(stmt.getBody(), funcEnv);
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return stmt) {
        stmt.getValue().accept(this);
        commands.add("return");
        return null;
    }

    @Override
    public Void visitExport(Statement.Export stmt) {
        return null;
    }

    private void executeBlock(List<Statement> statements, Environment env) {
        Environment previousEnv = this.currentEnv;

        this.currentEnv = env;

        for (Statement stmt : statements) {
            stmt.accept(this);
        }

        this.currentEnv = previousEnv;
    }
}
