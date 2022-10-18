package tech.jhamill34;

public interface FeatureFlags {
    boolean canUseExtendedCompiler();
    String scriptExtension();
    String templateExtension();
}
