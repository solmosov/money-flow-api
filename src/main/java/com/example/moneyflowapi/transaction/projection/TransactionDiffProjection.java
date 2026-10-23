package com.example.moneyflowapi.transaction.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionDiffProjection {
    Instant getDayName();
    BigDecimal getThisWeek();
    BigDecimal getLastWeek();
    BigDecimal getDiff();
    BigDecimal getDiffPercent();
}
