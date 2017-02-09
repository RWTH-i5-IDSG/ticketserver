package de.rwth.idsg.barti.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.rwth.idsg.barti.web.Constants;
import de.rwth.idsg.barti.web.interceptor.ResourceApiKeyHeaderInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Slf4j
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan("de.rwth.idsg.barti")
public class BeanConfiguration extends WebMvcConfigurerAdapter {

    private ScheduledThreadPoolExecutor executor;

    @PreDestroy
    public void shutDown() {
        if (executor != null) {
            gracefulShutDown(executor);
        }
    }

    private void gracefulShutDown(final ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

        } catch (final InterruptedException e) {
            log.error("Termination interrupted", e);

        } finally {
            if (!executor.isTerminated()) {
                log.warn("Killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("BarTi-Executor-%d")
                .build();

        executor = new ScheduledThreadPoolExecutor(10, threadFactory);
        return executor;
    }

    /**
     * add png converter first, otherwise json gives it a try and fails
     * <p>
     * since the DeferredResult stuff fails to identify that I want a PNG picture, just ignore the MediaType parameter
     * <p>
     * MappingJackson2HttpMessageConverter is needed to return plain JSON objects to AJAX calls.
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(createPictureProxyConverter());
        converters.add(getPNGConverter());
        converters.add(getJsonConverter());
    }

    private HttpMessageConverter<?> createPictureProxyConverter() {
        return new PictureProxyConverter();
    }

    private BufferedImageHttpMessageConverter getPNGConverter() {
        return new BufferedImageHttpMessageConverter() {
            @Override public void write(final BufferedImage image,
                                        final MediaType contentType,
                                        final HttpOutputMessage outputMessage)
                    throws IOException, HttpMessageNotWritableException {
                super.write(image, MediaType.IMAGE_PNG, outputMessage);
            }
        };
    }

    private MappingJackson2HttpMessageConverter getJsonConverter() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // ALLOW UNQUOTES FIELD NAMES
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        // JODA TIME
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JodaModule());

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new ResourceApiKeyHeaderInterceptor(Constants.SECURITY_API_KEY))
                .addPathPatterns("/**");
    }
}
