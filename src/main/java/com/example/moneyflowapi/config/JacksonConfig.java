package com.example.moneyflowapi.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

@Configuration
public class JacksonConfig {

    @Bean
    JsonMapperBuilderCustomizer bigDecimalCustomizer() {
        return builder -> builder.addModule(new SimpleModule()
                .addSerializer(BigDecimal.class, ToStringSerializer.instance)
        );
    }
}
