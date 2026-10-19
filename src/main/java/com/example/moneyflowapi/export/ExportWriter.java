package com.example.moneyflowapi.export;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;


public interface ExportWriter {
    void begin() throws IOException;

    void write(ExportRow row) throws IOException;

    void end() throws IOException;
}


