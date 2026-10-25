package com.example.moneyflowapi.export.imp;

import com.example.moneyflowapi.export.ExportFormat;
import com.example.moneyflowapi.export.ExportRow;
import com.example.moneyflowapi.export.ExportWriter;
import com.example.moneyflowapi.export.ExportWriterFactory;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Phrase;
import org.openpdf.text.DocumentException;
import org.openpdf.text.pdf.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class PdfExportWriterFactory implements ExportWriterFactory {


    @Override
    public ExportFormat format() {
        return ExportFormat.PDF;
    }

    @Override
    public ExportWriter create(OutputStream out, ZoneId zone, String downloadName) throws IOException {
        return new PdfExportWriter(out, zone);
    }


    static final class PdfExportWriter implements ExportWriter {
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        private static final int FLUSH_EVERY = 200;
        private static final int MAX_CELL_LENGTH = 80;

        private final Document document = new Document(PageSize.A4, 20, 20, 24, 24);
        private final PdfWriter pdfWriter;
        private final ZoneId zone;
        private final Font headerFont;
        private final Font cellFont;
        private PdfPTable table;
        private int rows;
        private boolean firstFlush = true;

        private static final byte[] FONT_BYTES = loadFont();

        private static byte[] loadFont() {
            try (InputStream in = new ClassPathResource("fonts/NotoSans-Regular.ttf").getInputStream()) {
                return in.readAllBytes();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        PdfExportWriter(OutputStream out, ZoneId zone) throws IOException {
            this.zone = zone;
            BaseFont base = BaseFont.createFont("NotoSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, FONT_BYTES, null);
            try {
                this.pdfWriter = PdfWriter.getInstance(document, new BufferedOutputStream(out, 65536));
                this.headerFont = new Font(base, 7, Font.BOLD, Color.WHITE);
                this.cellFont = new Font(base, 6.5f);
            } catch (DocumentException e) {
                throw new IOException(e);
            }
        }

        public void begin() throws IOException {
            try {
                document.open();
                table = newTable();
            } catch (DocumentException e) {
                throw new IOException(e);
            }
        }

        public void write(ExportRow row) throws IOException {
            addCell(DATE_FORMAT.format(row.date().atZone(zone)), Element.ALIGN_LEFT);
            addCell(zone.getId(), Element.ALIGN_LEFT);
            addCell(truncate(row.walletName()), Element.ALIGN_LEFT);
            addCell(truncate(row.walletType()), Element.ALIGN_LEFT);
            addCell(truncate(row.category()), Element.ALIGN_LEFT);
            addCell(truncate(row.type()), Element.ALIGN_LEFT);
            addCell(truncate(row.currencyCode()), Element.ALIGN_LEFT);
            addCell(decimal(row.amount()), Element.ALIGN_RIGHT);
            addCell(decimal(row.rateAmount()), Element.ALIGN_RIGHT);
            addCell(truncate(row.description()), Element.ALIGN_LEFT);
            addCell(row.date().toString(), Element.ALIGN_LEFT);
            if (++rows % FLUSH_EVERY == 0) {
                flushTable(false);
            }
        }

        public void end() throws IOException {
            flushTable(true);
        }

        public void close() {
            if (document.isOpen()) {
                document.close();
            }
            pdfWriter.close();
        }

        private static String decimal(BigDecimal value) {
            return value == null ? "" : String.format(Locale.US, "%,.2f", value);
        }

        private PdfPTable newTable() throws DocumentException {
            PdfPTable t = new PdfPTable(new float[]{
                    2.2f,  // date
                    2.0f,  // time_zone
                    2.0f,  // wallet_name
                    1.3f,  // wallet_type
                    2.0f,  // category_name
                    1.3f,  // type
                    1.1f,  // currency
                    1.8f,  // amount
                    1.3f,  // rate_amount
                    3.0f,  // description
                    3.0f   // raw_date
            });
            t.setWidthPercentage(100);
            t.setHeaderRows(1);
            t.setSkipFirstHeader(!firstFlush);
            t.setComplete(false);
            for (String h : new String[]{"date", "time_zone", "wallet_name", "wallet_type",
                    "category_name", "type", "currency", "amount", "rate_amount",
                    "description", "raw_date"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                c.setBackgroundColor(new Color(55, 65, 81));
                c.setPadding(3);
                t.addCell(c);
            }
            return t;
        }

        private void addCell(String text, int align) {
            PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text, cellFont));
            c.setHorizontalAlignment(align);
            c.setPadding(3);
            table.addCell(c);
        }

        private void flushTable(boolean last) throws IOException {
            try {
                if (last) {
                    table.setComplete(true);
                }
                document.add(table);
                if (!last) {
                    firstFlush = false;
                    table.deleteBodyRows();
                    table.setSkipFirstHeader(true);
                }
            } catch (DocumentException e) {
                throw new IOException(e);
            }
        }

        private static String truncate(String s) {
            if (s == null) return "";
            return s.length() > MAX_CELL_LENGTH ? s.substring(0, MAX_CELL_LENGTH) : s;
        }
    }

    private static final class WatermarkEvent extends PdfPageEventHelper {
        private static final String TEXT = "pulqadam.uz";
        private final BaseFont base;
        private final PdfGState gState;

        WatermarkEvent(BaseFont base) {
            this.base = base;
            this.gState = new PdfGState();
            this.gState.setFillOpacity(0.12f);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContentUnder();
            float x = document.getPageSize().getWidth() / 2;
            float y = document.getPageSize().getHeight() / 2;
            cb.saveState();
            cb.setGState(gState);
            cb.beginText();
            cb.setFontAndSize(base, 64);
            cb.setColorFill(new Color(55, 65, 81));
            cb.showTextAligned(Element.ALIGN_CENTER, TEXT, x, y, 45);
            cb.endText();
            cb.restoreState();
        }
    }
}
