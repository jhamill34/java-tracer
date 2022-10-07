package tech.jhamill34.path;

import java.io.InputStream;

public interface FileProcessor {
    void process(String name, String parent, InputStream inputStream);
}
