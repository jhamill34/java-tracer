package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import tech.jhamill34.FeatureFlags;
import tech.jhamill34.flags.PropertyFeatureFlags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FeatureFlagsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FeatureFlags.class).to(PropertyFeatureFlags.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Named("features")
    public Properties provideProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("features.properties")){
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties;
        } catch (IOException e) {
            return null;
        }
    }
}
