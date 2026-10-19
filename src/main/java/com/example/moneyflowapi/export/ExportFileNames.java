package com.example.moneyflowapi.export;

import java.time.*;

public final class ExportFileNames {
    public static final String BRAND_NAME = "pulqadam";

    private ExportFileNames() {
    }

    public static ZoneId parseZone(String value) {
        try {
            return ZoneId.of(value);
        } catch (DateTimeException e) {
            return ZoneOffset.UTC;
        }
    }

    public static String transactions(Instant from, Instant to, ZoneId zone, Instant createdAt, ExportFormat format) {
        int year = from.atZone(zone).getYear();
        return BRAND_NAME + "_" + year + downloadExtension(format);
    }

    public static String downloadExtension(ExportFormat format) {
        return format == ExportFormat.PDF ? ".pdf" : ".zip";
    }

    public static String csvName(String zipName) {
        return zipName.substring(0, zipName.length() - ".zip".length()) + ".csv";
    }
}