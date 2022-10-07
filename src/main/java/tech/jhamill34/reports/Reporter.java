package tech.jhamill34.reports;

public interface Reporter extends AutoCloseable {
    void write(String value) throws Exception;
}
