package com.example.moneyflowapi.export.dto.internal;

import com.example.moneyflowapi.export.entity.ExportJob;

public record ExportJobResult(ExportJob job, boolean created) {
}