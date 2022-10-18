package tech.jhamill34.repl.extensions.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class Template {
    public abstract <R> R accept(Visitor<R> visitor);

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class PlainText extends Template {
        final String literal;
        final Template next;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPlainText(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class PrintableExpression extends Template {
        final Expression expression;
        final Template next;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintableExpression(this);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    public static class Empty extends Template {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitEmpty(this);
        }
    }

    public interface Visitor<R> {
        R visitPlainText(PlainText tmp);
        R visitPrintableExpression(PrintableExpression tmp);
        R visitEmpty(Empty tmp);
    }
}
