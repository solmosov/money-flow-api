package com.example.moneyflowapi.export;


import java.math.BigDecimal;
import java.time.Instant;

public record ExportRow(Instant at, String category, String type, BigDecimal amount, String description) {
}
