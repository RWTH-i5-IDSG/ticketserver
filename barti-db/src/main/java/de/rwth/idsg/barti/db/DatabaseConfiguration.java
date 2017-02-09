package de.rwth.idsg.barti.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Configuration
@Log4j2
//@org.springframework.scheduling.annotation.EnableAsync
public class DatabaseConfiguration {
    private HikariDataSource dataSource;
    private JdbcAppender appender;

    static {
        // FIXME disable jooq logo
        System.setProperty("org.jooq.no-logo", "true");
    }

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
     */
    private HikariDataSource initDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(org.postgresql.ds.PGSimpleDataSource.class.getName());

        final PropertiesHolder.DBConfig dbConfig = PropertiesHolder.CONFIG.getDbConfig();
        config.addDataSourceProperty("serverName", dbConfig.getHost());
        config.addDataSourceProperty("portNumber", dbConfig.getPort());
        config.addDataSourceProperty("databaseName", dbConfig.getDbname());
        config.addDataSourceProperty("user", dbConfig.getUser());
        config.addDataSourceProperty("password", dbConfig.getPassword());

        return new HikariDataSource(config);
    }

    /**
     * initialize data source and jdbc appender
     * http://stackoverflow.com/a/30598749/3661794
     */
    @PostConstruct
    private void init() {
        dataSource = initDataSource();
        log.info("initializing logging to database...");
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        final ColumnConfig[] cc = {
                ColumnConfig.createColumnConfig(config, "logtime", null, null, "true", null, null),
                ColumnConfig.createColumnConfig(config, "deployment", null,
                        Integer.toString(de.rwth.idsg.barti.db.Util.DEPLOYMENT),
                        "false", null, null),
                ColumnConfig.createColumnConfig(config, "level", "%level", null, null, "false", null),
                ColumnConfig.createColumnConfig(config, "logger", "%logger", null, null, "false", null),
                ColumnConfig.createColumnConfig(config, "message", "%message", null, null, "false", null),
                ColumnConfig.createColumnConfig(config, "throwable", "%ex{full}", null, null, "false", null)
        };
        appender = JdbcAppender.createAppender("databaseAppender", "true", null,
                dataSource::getConnection, "0", "public.operations_log", cc);
        appender.start();
        config.addAppender(appender);
        final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.addAppender(appender, Level.INFO, null);

        ctx.updateLoggers();
    }

    /**
     * Can we re-use DSLContext as a Spring bean (singleton)? Yes, the Spring tutorial of
     * Jooq also does it that way, but only if we do not change anything about the
     * config after the init (which we don't do anyways) and if the ConnectionProvider
     * does not store any shared state (we use DataSourceConnectionProvider of Jooq, so no problem).
     * <p>
     * Some sources and discussion:
     * - http://www.jooq.org/doc/3.6/manual/getting-started/tutorials/jooq-with-spring/
     * - http://jooq-user.narkive.com/2fvuLodn/dslcontext-and-threads
     * - https://groups.google.com/forum/#!topic/jooq-user/VK7KQcjj3Co
     * - http://stackoverflow.com/questions/32848865/jooq-dslcontext-correct-autowiring-with-spring
     */
    @Bean
    public DSLContext dslContext() {
        return DSL.using(new DefaultConfiguration()
                .set(SQLDialect.POSTGRES_9_5)
                .set(new DataSourceConnectionProvider(dataSource))
                .set(new Settings().withExecuteLogging(true)));
    }

    @PreDestroy
    public void shutDown() {
        log.info("stopping logging to database");
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.removeAppender("databaseAppender");
        ctx.updateLoggers();
        appender.stop();
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
