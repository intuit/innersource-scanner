package com.github.intuit.innersource.reposcanner.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Properties;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Immutable(singleton = true, builder = false)
@Style(visibility = ImplementationVisibility.PACKAGE)
abstract class BuildInfoService {

    public static BuildInfoService getInstance() {
        return ImmutableBuildInfoService.of();
    }

    @Lazy
    Properties buildProperties() {
        try {
            final URL url = getClass().getResource("/build.properties");
            if (url == null) {
                throw new RuntimeException(
                    "build.properties file is missing, malformed distribution"
                );
            }
            final Properties buildProperties = new Properties();
            buildProperties.load(url.openStream());
            return buildProperties;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getProperty(final String propertyName) {
        return buildProperties().getProperty(propertyName);
    }
}
