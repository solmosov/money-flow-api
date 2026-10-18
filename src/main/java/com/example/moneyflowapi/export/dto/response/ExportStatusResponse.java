package com.example.moneyflowapi.export.dto.response;

public record ExportStatusResponse(
        String status,
        int percent,
        String message
) {
}
