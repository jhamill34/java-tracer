package tech.jhamill34.flags;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tech.jhamill34.FeatureFlags;

import java.util.Properties;

public class PropertyFeatureFlags implements FeatureFlags {
    @Inject
    @Named("features")
    private Properties properties;

    @Override
    public boolean canUseExtendedCompiler() {
         String value = properties.getProperty("compiler.useExtended", "false");
         return value.equals("true");
    }

    @Override
    public String scriptExtension() {
        return properties.getProperty("compiler.extension", "jt");
    }

    @Override
    public String templateExtension() {
        return properties.getProperty("template.extension", "jte");
    }
}
