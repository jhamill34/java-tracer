package tech.jhamill34.entities;

public interface Entity {
    int getId();
    String accept(EntityVisitor<String> entityVisitor);
}
