package tech.jhamill34.repl.extensions;

import com.google.common.io.Files;
import tech.jhamill34.repl.extensions.nodes.Expression;
import tech.jhamill34.repl.extensions.nodes.Program;
import tech.jhamill34.repl.extensions.nodes.Statement;
import tech.jhamill34.repl.extensions.nodes.Template;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CodeGenerator implements Program.Visitor<Void>, Statement.Visitor<Void>, Expression.Visitor<Void>, Template.Visitor<Void> {
    private static int labelId = 0;
    private Environment currentEnv = null;
    private final Set<String> functions = new HashSet<>();
    private final List<String> commands = new ArrayList<>();
    private final Set<String> includedFiles = new HashSet<>();

    private final Map<Object, Integer> constants = new HashMap<>();
    private static int constantId = 0;

    private final ASTPipeline pipeline;
    private final int argc;

    public CodeGenerator(ASTPipeline pipeline, int argc) {
        this.pipeline = pipeline;
        this.argc = argc;
    }

    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("" + constants.size());
        for (Map.Entry<Object, Integer> entry : constants.entrySet()) {
            Object value = entry.getKey();
            String type = "_";
            if (value instanceof Integer) {
               type = "I";
            } else if (value instanceof String) {
                type = "S";
            } else if (value instanceof Boolean) {
                type = "B";
            }
            result.add("#" + entry.getValue() + ":" + type + entry.getKey());
        }

        result.add("" + commands.size());
        result.addAll(commands);

        return result;
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
        Object value = expr.getValue();

        if (!constants.containsKey(value)) {
            constants.put(value, constantId++);
        }

        int id = constants.get(value);

        commands.add("push #" + id);
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
    public Void visitNoOp(Expression.NoOp expr) {
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

        if (!(stmt.getInitializer() instanceof Expression.NoOp)) {
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
        stmt.getElseBranch().accept(this);
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
        commands.add("queue L");

        Environment previousEnv = this.currentEnv;
        currentEnv = Environment.of(this.currentEnv);
        // Decalre our item variable
        currentEnv.declare(stmt.getItem().getLexeme());

        commands.add("goto condition" + currentLabel);

        commands.add("loop" + currentLabel + ":");
        commands.add("dup");
        commands.add("next");
        commands.add("store " + currentEnv.find(stmt.getItem().getLexeme()));
        stmt.getBody().accept(this);
        commands.add("condition" + currentLabel + ":");
        commands.add("dup");
        commands.add("attr size");
        commands.add("push 0");
        commands.add("cmp >");
        commands.add("jmp loop" + currentLabel);
        commands.add("pop");

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

                // NOTE: At the moment you can't import a "template"
                Program subProgram = pipeline.execute(content, false);

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

    @Override
    public Void visitNoOp(Statement.NoOp stmt) {
        return null;
    }

    @Override
    public Void visitTemplateStatement(Statement.TemplateStatement stmt) {
        stmt.getTemplate().accept(this);
        return null;
    }

    @Override
    public Void visitPrintableExpression(Template.PrintableExpression stmt) {
        stmt.getExpression().accept(this);
        commands.add("print");
        stmt.getNext().accept(this);
        return null;
    }

    @Override
    public Void visitPlainText(Template.PlainText stmt) {
        if (!constants.containsKey(stmt.getLiteral())) {
            constants.put(stmt.getLiteral(), constantId++);
        }

        commands.add("push #" + constants.get(stmt.getLiteral()));
        commands.add("print");

        stmt.getNext().accept(this);

        return null;
    }

    @Override
    public Void visitEmpty(Template.Empty tmp) {
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

        if (!constants.containsKey("\n")) {
            constants.put("\n", constantId++);
        }
        functions.add("println");
        commands.addAll(Arrays.asList(
                "println:",
                "push #" + constants.get("\n"),
                "math +",
                "print",
                "return"
        ));

        functions.add("queue");
        commands.addAll(Arrays.asList(
                "queue:",
                "queue",
                "return"
        ));

        functions.add("asQueue");
        commands.addAll(Arrays.asList(
                "asQueue:",
                "queue L",
                "return"
        ));

        functions.add("poll");
        commands.addAll(Arrays.asList(
                "poll:",
                "next",
                "return"
        ));

        functions.add("offer");
        commands.addAll(Arrays.asList(
                "offer:",
                "offer",
                "return"
        ));

        functions.add("allClasses");
        commands.addAll(Arrays.asList(
                "allClasses:",
                "list C",
                "return"
        ));

        functions.add("allMethods");
        commands.addAll(Arrays.asList(
                "allMethods:",
                "list M",
                "return"
        ));

        functions.add("allFields");
        commands.addAll(Arrays.asList(
                "allFields:",
                "list F",
                "return"
        ));

        functions.add("allValues");
        commands.addAll(Arrays.asList(
                "allValues:",
                "list V",
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
