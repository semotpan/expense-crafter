package com.semotpan.expensecrafter;

import com.semotpan.expensecrafter.shared.ApiFailureHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(WebConfig.CorsProperties.class)
class WebConfig {

    @Bean
    ApiFailureHandler apiFailureHandler() {
        return new ApiFailureHandler();
    }

    @Bean
    WebMvcConfigurer corsConfigurer(CorsProperties properties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                var config = new CorsConfiguration();
                config.setAllowedOrigins(properties.getAllowedOrigins());
                config.setAllowedMethods(properties.getAllowedMethods());
                config.setAllowedHeaders(properties.getAllowedHeaders());
                config.setExposedHeaders(properties.getExposedHeaders());
                config.setAllowCredentials(properties.getAllowCredentials());
                config.setMaxAge(properties.getMaxAge());

                registry.addMapping("/**").combine(config);
            }
        };
    }

    @Setter
    @Getter
    @ConfigurationProperties(prefix = "web.cors")
    static final class CorsProperties {

        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> allowedHeaders = new ArrayList<>();
        private List<String> exposedHeaders = new ArrayList<>();
        private Boolean allowCredentials;
        private Duration maxAge = Duration.ofSeconds(1800);

    }
}
