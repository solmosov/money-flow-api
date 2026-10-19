package com.example.moneyflowapi.export.imp;

import com.example.moneyflowapi.export.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
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

        private static String csv(String value) {
            if (value == null || value.isEmpty()) {
                return "";
            }
            String cleaned = sanitize(value).strip();
            if (cleaned.isEmpty()) {
                return "";
            }
            String folded = Normalizer.normalize(cleaned, Normalizer.Form.NFKC).stripLeading();
            if (!folded.isEmpty() && FORMULA_PREFIXES.indexOf(folded.charAt(0)) >= 0) {
                cleaned = "'" + cleaned;
            }
            return '"' + cleaned.replace("\"", "\"\"") + '"';
        }

        private static String sanitize(String value) {
            StringBuilder sb = new StringBuilder(Math.min(value.length(), MAX_CELL_LENGTH));
            int count = 0;
            int i = 0;
            while (i < value.length() && count < MAX_CELL_LENGTH) {
                int cp = value.codePointAt(i);
                i += Character.charCount(cp);
                if (cp == '\n' || cp == '\r' || cp == '\t') {
                    sb.append(' ');
                    count++;
                    continue;
                }
                switch (Character.getType(cp)) {
                    case Character.SPACE_SEPARATOR -> {
                        sb.append(' ');
                        count++;
                    }
                    case Character.CONTROL, Character.FORMAT, Character.LINE_SEPARATOR,
                         Character.PARAGRAPH_SEPARATOR, Character.PRIVATE_USE,
                         Character.SURROGATE, Character.UNASSIGNED -> {
                    }
                    default -> {
                        sb.appendCodePoint(cp);
                        count++;
                    }
                }
            }
            return sb.toString();
        }
    }
}
