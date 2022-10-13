package tech.jhamill34.repl.extensions;

import com.google.common.io.Files;
import tech.jhamill34.repl.extensions.nodes.Expression;
import tech.jhamill34.repl.extensions.nodes.Program;
import tech.jhamill34.repl.extensions.nodes.Statement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeGenerator implements Program.Visitor<Void>, Statement.Visitor<Void>, Expression.Visitor<Void> {
    private static int labelId = 0;
    private Environment currentEnv = null;
    private final Set<String> functions = new HashSet<>();
    private final List<String> commands = new ArrayList<>();
    private final ASTPipeline pipeline;
    private final int argc;
    private final Set<String> includedFiles = new HashSet<>();

    public CodeGenerator(ASTPipeline pipeline, int argc) {
        this.pipeline = pipeline;
        this.argc = argc;
    }

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

        for (Statement include : program.getIncludes()) {
            include.accept(this);
        }

        // Add built in methods here
        executeBlock(program.getFunctionDeclarations(), root);

        commands.add("main:");

        for (int i = argc; i > 0; i--) {
            root.declare("$" + i);
            commands.add("store " + root.find("$" + i));
        }

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
    public Void visitForEach(Statement.ForEach stmt) {
        int currentLabel = labelId++;
        stmt.getIterable().accept(this);
        commands.add("expand");

        Environment previousEnv = this.currentEnv;
        currentEnv = Environment.of(this.currentEnv);
        // Decalre our item variable
        currentEnv.declare(stmt.getItem().getLexeme());

        // Prepare our end condition value
        currentEnv.declare("_total");
        commands.add("store " + currentEnv.find("_total"));

        // Prepare our counter
        currentEnv.declare("_idx");
        commands.add("push 0");
        commands.add("store " + currentEnv.find("_idx"));

        // Jump to our condition evaluation
        commands.add("goto condition" + currentLabel);

        // The main body
        commands.add("body" + currentLabel + ":");
        commands.add("store " + currentEnv.find(stmt.getItem().getLexeme()));
        stmt.getBody().accept(this);

        // increment _idx
        commands.add("load " + currentEnv.find("_idx"));
        commands.add("push 1");
        commands.add("math +");
        commands.add("store " + currentEnv.find("_idx"));

        // Our continue condition
        commands.add("condition" + currentLabel + ":");
        commands.add("load " + currentEnv.find("_idx"));
        commands.add("load " + currentEnv.find("_total"));
        commands.add("cmp =");
        commands.add("jmp end" + currentLabel);
        commands.add("goto body" + currentLabel);
        commands.add("end" + currentLabel + ":");

        currentEnv = previousEnv;
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

    @Override
    public Void visitInclude(Statement.Include include) {
        String fileName = include.getFile().getLiteral().toString();

        if (includedFiles.contains(fileName)) {
            return null;
        }

        if (fileName.equals("native")) {
            includeNative();
            includedFiles.add(fileName);
        } else {
            try {
                String content = Files.asCharSource(new File(fileName), StandardCharsets.UTF_8).read();
                Program subProgram = pipeline.execute(content);

                for (Statement stmt : subProgram.getIncludes()) {
                    stmt.accept(this);
                }

                for (Statement stmt : subProgram.getFunctionDeclarations()) {
                    stmt.accept(this);
                }
                includedFiles.add(fileName);
            } catch (IOException | ParserException | TokenizerException e) {
                System.err.println(e.getMessage());
            }
        }

        return null;
    }

    private void includeNative() {
        functions.add("getClass");
        commands.addAll(Arrays.asList(
                "getClass:",
                "push C",
                "id",
                "push C",
                "find",
                "return"
        ));

        functions.add("getMethod");
        commands.addAll(Arrays.asList(
                "getMethod:",
                "push M",
                "id",
                "push M",
                "find",
                "return"
        ));

        functions.add("getField");
        commands.addAll(Arrays.asList(
                "getField:",
                "push F",
                "id",
                "push F",
                "find",
                "return"
        ));

        functions.add("simulate");
        commands.addAll(Arrays.asList(
                "simulate:",
                "invoke",
                "return"
        ));

        functions.add("print");
        commands.addAll(Arrays.asList(
                "print:",
                "print",
                "return"
        ));
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
