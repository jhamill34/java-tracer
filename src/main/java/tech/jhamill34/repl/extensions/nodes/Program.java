package tech.jhamill34.repl.extensions.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class Program {
    final List<Statement> statements;
    final List<Statement> includes;
    final List<Statement> functionDeclarations;
    final Statement exportStatement;

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitProgram(this);
    }

    public interface Visitor<R> {
        R visitProgram(Program program);
    }
}
