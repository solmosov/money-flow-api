package com.example.moneyflowapi.transaction.dto.response;

import com.example.moneyflowapi.transaction.projection.TransactionDiffProjection;

import java.util.List;

public record TransactionDiffStatResponse(
    List<TransactionDiffProjection> data
) {
}
