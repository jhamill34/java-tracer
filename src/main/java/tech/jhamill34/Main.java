package tech.jhamill34;

import com.google.inject.Guice;
import tech.jhamill34.modules.MainModule;

public class Main {
    public static void main(String[] args) {
        Guice.createInjector(new MainModule())
                .getInstance(Application.class)
                .start(args);
    }
}
