package de.rwth.idsg.barti.web;

import java8.util.Optional;
import java8.util.function.Function;
import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public final class PropertiesHolder {
    public static final PropertiesHolder CONFIG = new PropertiesHolder();

    public static final String SETTINGS = "barti-web.properties";

    // Root mapping for Spring
    private String springMapping = "/*";
    private String contextPath = "/";

    final Properties properties;

    private Jetty jetty;

    private PropertiesHolder() {
        final Path searchProperties = searchProperties();
        final Path filePath = Optional.ofNullable(searchProperties).orElse(Paths.get(SETTINGS));
        this.properties = new Properties();
        if (null != searchProperties) {
            try {
                this.properties.load(Files.newBufferedReader(filePath));
            } catch (final IOException e) {
                throw new Error(e);
            }
        }
        jetty = Jetty.builder()
                .serverHost(get("server.host"))
                .gzipEnabled(getBoolean("server.gzip.enabled"))
                .httpEnabled(getBoolean("http.enabled"))
                .httpPort(getInt("http.port"))
                .httpsEnabled(getBoolean("https.enabled"))
                .httpsPort(getInt("https.port"))
                .keyStorePath(get("keystore.path"))
                .keyStorePassword(get("keystore.password"))
                .build();
    }

    private static Path searchProperties() {
        final String fromEnv = System.getenv(SETTINGS);
        if (null != fromEnv && !fromEnv.isEmpty()) return Paths.get(fromEnv);
        final String fromSys = System.getProperty(SETTINGS);
        if (null != fromSys && !fromSys.isEmpty()) return Paths.get(fromSys);
        final Path def = Paths.get(SETTINGS);
        if (null != def && Files.exists(def)) {
            return def;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String get(final String key) {
        return (String) properties.get(key);
    }

    public <T> T getOrDefault(final String key, final Function<String, T> mapper, final T defaultValue) {
        return Optional.ofNullable(get(key)).map(mapper).orElse(defaultValue);
    }

    public String getOrDefault(final String key, final String defaultValue) {
        return Optional.ofNullable(get(key)).orElse(defaultValue);
    }

    public static Integer parseUnsignedInt(final String input) {
        if (input.startsWith("0x")) {
            return Integer.parseUnsignedInt(input.substring(2), 16);
        }
        return Integer.parseUnsignedInt(input, 10);
    }

    public Integer getOrDefaultInt(final String key, final Integer defaultValue) {
        return getOrDefault(key, PropertiesHolder::parseUnsignedInt, defaultValue);
    }

    public Integer getInt(final String key) {
        return getOrDefaultInt(key, null);
    }

    public Boolean getOrDefaultBoolean(final String key, final Boolean defaultValue) {
        return getOrDefault(key, Boolean::parseBoolean, defaultValue);
    }

    public Boolean getBoolean(final String key) {
        return getOrDefaultBoolean(key, null);
    }


    // -------------------------------------------------------------------------
    // Class declarations
    // -------------------------------------------------------------------------

    // Jetty configuration
    @Builder
    @Getter
    public static class Jetty {
        private String serverHost;
        private boolean gzipEnabled;

        // HTTP
        private boolean httpEnabled;
        private int httpPort;

        // HTTPS
        private boolean httpsEnabled;
        private int httpsPort;
        private String keyStorePath;
        private String keyStorePassword;
    }
}
