package tech.jhamill34.pico;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import picocli.CommandLine;

public class GuiceFactory implements CommandLine.IFactory {
    private final Injector injector;

    @Inject
    public GuiceFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        try {
            return injector.getInstance(cls);
        } catch (ConfigurationException e) {
            return CommandLine.defaultFactory().create(cls);
        }
    }
}
