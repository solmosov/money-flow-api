package com.example.moneyflowapi.export;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class ExportFileNames {
    static final String BRAND_NAME = "pulqadam";

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private ExportFileNames() {
    }

    public static ZoneId parseZone(String value) {
        try {
            return ZoneId.of(value);
        } catch (DateTimeException e) {
            return ZoneOffset.UTC;
        }
    }

    public static String transactions(Instant from, Instant to, ZoneId zone, Instant createdAt) {
        LocalDate fromDate = from.atZone(zone).toLocalDate();
        LocalDate toDate = to.minusMillis(1).atZone(zone).toLocalDate();
        return BRAND_NAME +"_" + DATE.format(fromDate) + "_to_" + DATE.format(toDate)
                + "_" + STAMP.format(createdAt.atZone(zone)) + ".zip";
    }

    public static String csvName(String zipName) {
        return zipName.substring(0, zipName.length() - ".zip".length()) + ".csv";
    }
}
