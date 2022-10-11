package tech.jhamill34.repl.commands.attributes;

public interface Query {
   Object query(String input) throws QueryException;
}
