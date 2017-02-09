package de.rwth.idsg.barti.db;

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

    public static final String SETTINGS = "barti-db.properties";

    final Properties properties;

    final DBConfig dbConfig;

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
        this.dbConfig = DBConfig.builder()
                .host(get("db.host", "localhost"))
                .port(get("db.port", "5432"))
                .dbname(get("db.dbname", "barti_pseudouic"))
                .schema(get("db.schema", "public"))
                .user(get("db.user", "barti_user"))
                .password(get("db.password", ""))
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
    public String get(final String key, final String defaultValue) {
        final String value = (String) properties.get(key);
        if (null == value) {
            return defaultValue;
        }
        return value;
    }
    // -------------------------------------------------------------------------
    // Class declarations
    // -------------------------------------------------------------------------

    // Database configuration
    @Builder
    @Getter
    public static class DBConfig {
        private String host;
        private String port;
        private String dbname;
        private String schema;
        private String user;
        private String password;
    }
}
