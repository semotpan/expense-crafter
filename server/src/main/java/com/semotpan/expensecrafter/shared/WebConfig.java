package com.semotpan.expensecrafter.shared;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
class WebConfig {

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
}
