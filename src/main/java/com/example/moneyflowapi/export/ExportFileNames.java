package com.example.moneyflowapi.export;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class ExportFileNames {
    static final String BRAND_NAME = "pulqadam";

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
        int year = from.atZone(zone).getYear();
        return BRAND_NAME + "_" + year + ".zip";
    }

    public static String csvName(String zipName) {
        return zipName.substring(0, zipName.length() - ".zip".length()) + ".csv";
    }
}
