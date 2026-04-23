package com.massemiso.supermarket_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * Configuration class for the application.
 * Enables Spring Data Web support.
 * Configures the page serialization mode to use DTOs.
 * This is required for pagination to return DTOs instead of entities.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig {

}
