package com.example.moneyflowapi.telegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Chat(
        Long id,
        String type
) {}