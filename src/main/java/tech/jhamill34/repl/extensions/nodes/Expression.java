package tech.jhamill34.repl.extensions.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.jhamill34.repl.extensions.Token;

import java.util.List;

public abstract class Expression {
    public abstract <R> R accept(Visitor<R> visitor);

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Binary extends Expression {
        final Expression left;
        final Token operator;
        final Expression right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Unary extends Expression {
        final Token operator;
        final Expression expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Literal extends Expression {
        final Object value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Identifier extends Expression {
        final Token value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIdentifier(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Grouping extends Expression {
        final Expression group;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGrouping(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Call extends Expression {
        final Expression callee;
        final Token paren;
        final List<Expression> arguments;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCall(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Assign extends Expression {
        final Token name;
        final Expression value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssign(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Get extends Expression {
        final Expression object;
        final Token name;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGet(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class NoOp extends Expression {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitNoOp(this);
        }
    }

    public interface Visitor<R> {
        R visitBinary(Binary expr);
        R visitUnary(Unary expr);
        R visitLiteral(Literal expr);
        R visitIdentifier(Identifier expr);
        R visitGrouping(Grouping expr);
        R visitCall(Call expr);
        R visitAssign(Assign expr);
        R visitGet(Get expr);
        R visitNoOp(NoOp expr);
    }
}
