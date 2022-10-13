package tech.jhamill34.repl.extensions.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.jhamill34.repl.extensions.Token;

import java.util.List;
import java.util.Map;

public abstract class Statement {
    public abstract <R> R accept(Visitor<R> visitor);

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class ExprStatement extends Statement {
        final Expression expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExprStatement(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Var extends Statement {
        final Token name;
        final Expression initializer;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVar(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Block extends Statement {
        final List<Statement> statements;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlock(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class If extends Statement {
        final Expression condition;
        final Statement thenBranch;
        final Statement elseBranch;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIf(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class While extends Statement {
        final Expression condition;
        final Statement body;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhile(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class FunctionDecl extends Statement {
        final Token name;
        final List<Token> params;
        final List<Statement> body;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunction(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Return extends Statement {
        final Token keyword;
        final Expression value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturn(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Export extends Statement {
        final List<Token> tokens;
        final Map<Token, Expression> computedTokens;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExport(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Include extends Statement {
        final Token keyword;
        final Token file;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitInclude(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class ForEach extends Statement {
        final Token item;
        final Expression iterable;
        final Statement body;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForEach(this);
        }
    }
    

    public interface Visitor<R> {
        R visitExprStatement(ExprStatement stmt);
        R visitVar(Var stmt);
        R visitBlock(Block block);
        R visitIf(If stmt);
        R visitWhile(While stmt);
        R visitFunction(FunctionDecl stmt);
        R visitReturn(Return stmt);
        R visitExport(Export stmt);
        R visitInclude(Include stmt);
        R visitForEach(ForEach stmt);
    }
}
