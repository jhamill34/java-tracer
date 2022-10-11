package tech.jhamill34.analyze;

public interface ArrayHandler {
    void alloc(IdValue ref);

    void store(IdValue ref, IdValue value);

    IdValue load(IdValue ref, IdValue defaultValue);
}
