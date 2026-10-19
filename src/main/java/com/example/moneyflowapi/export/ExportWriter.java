package com.example.moneyflowapi.export;

import java.io.Closeable;
import java.io.IOException;


public interface ExportWriter extends Closeable {
    void begin() throws IOException;

    void write(ExportRow row) throws IOException;

    void end() throws IOException;
}


