package com.example.moneyflowapi.telegram.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        @JsonProperty("message_id") Long messageId,
        User from,
        Chat chat,
        Long date,
        String text
) {}
