package com.example.moneyflowapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer(){
        return resolver -> {
            resolver.setMaxPageSize(50);
            resolver.setOneIndexedParameters(false);
            resolver.setPageParameterName("page");
            resolver.setSizeParameterName("size");
        };
    }
}
