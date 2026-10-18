package com.example.moneyflowapi.export.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.export")
public record ExportProperties(String dir, @DefaultValue("24") int ttlHours) {

    public Path path(){
        return Path.of(dir).toAbsolutePath();
    }
}
