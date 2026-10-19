package com.example.moneyflowapi.export;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;

public interface ExportWriterFactory {
    ExportFormat format();

    ExportWriter create(OutputStream out, ZoneId zone, String downloadName) throws IOException;
}