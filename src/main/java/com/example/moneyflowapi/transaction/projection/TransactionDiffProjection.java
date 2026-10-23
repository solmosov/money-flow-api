package com.example.moneyflowapi.transaction.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface TransactionDiffProjection {
    Integer getBucketIndex();
    Instant getDayName();
    BigDecimal getLastPeriod();
    BigDecimal getThisPeriod();
    BigDecimal getDiff();
    BigDecimal getDiffPercent();
}
