package com.example.moneyflowapi.export.imp;

import com.example.moneyflowapi.export.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class CsvExportWriterFactory implements ExportWriterFactory {

    @Override
    public ExportFormat format() {
        return ExportFormat.CSV;
    }

    @Override
    public ExportWriter create(OutputStream out, ZoneId zone, String downloadName) {
        return new CsvExportWriter(out, zone, downloadName);
    }

    static final class CsvExportWriter implements ExportWriter {
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        private static final String HEADER = "date,time_zone,category_name,type,amount,description,raw_date\n";
        private static final int MAX_CELL_LENGTH = 1000;
        private static final String FORMULA_PREFIXES = "=+-@";

        private final ZipOutputStream zip;
        private final Writer writer;
        private final ZoneId zone;
        private final String entryName;

        CsvExportWriter(OutputStream out, ZoneId zone, String downloadName) {
            this.zip = new ZipOutputStream(new BufferedOutputStream(out, 65536));
            this.writer = new BufferedWriter(new OutputStreamWriter(zip, StandardCharsets.UTF_8), 65536);
            this.zone = zone;
            this.entryName = ExportFileNames.csvName(downloadName);
        }

        public void begin() throws IOException {
            zip.putNextEntry(new ZipEntry(entryName));
            writer.write('\uFEFF');
            writer.write(HEADER);
        }

        public void write(ExportRow row) throws IOException {
            writer.write(DATE_FORMAT.format(row.at().atZone(zone)) + ","
                    + zone.getId() + ","
                    + csv(row.category()) + ","
                    + csv(row.type()) + ","
                    + (row.amount() == null ? "" : row.amount().toPlainString()) + ","
                    + csv(row.description()) + ","
                    + row.at() + "\n");
        }

        public void end() throws IOException {
            writer.flush();
            zip.closeEntry();
        }

        public void close() throws IOException {
            writer.close();
        }

        private static String csv(String value) { /* mavjud csv() metodingiz */ return ""; }
        private static String sanitize(String value) { /* mavjud sanitize() metodingiz */ return ""; }
    }
}
