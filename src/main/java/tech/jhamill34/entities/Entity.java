package tech.jhamill34.entities;

public interface Entity {
    int getId();
    <T> T accept(EntityVisitor<T> entityVisitor);
}
