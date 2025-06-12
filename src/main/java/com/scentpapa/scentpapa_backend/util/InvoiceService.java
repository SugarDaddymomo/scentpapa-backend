package com.scentpapa.scentpapa_backend.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.scentpapa.scentpapa_backend.models.Order;
import com.scentpapa.scentpapa_backend.models.OrderItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InvoiceService {

    public ByteArrayInputStream generateInvoice(Order order) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            //ADD Invoice title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("INVOICE - " + order.getReferenceNumber(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            //ADD Order meta info
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String formattedDate = order.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            document.add(new Paragraph("Date: " + formattedDate, infoFont));
            document.add(new Paragraph("Customer: " + order.getUser().getFirstName(), infoFont));
            String shippingAddress = order.getShippingAddress().getResidenceName() + ", " +
                    order.getShippingAddress().getStreetAddress() + ", " +
                    order.getShippingAddress().getCity() + ", " +
                    order.getShippingAddress().getState() + " - " +
                    order.getShippingAddress().getPostalCode() + ", " +
                    order.getShippingAddress().getCountry();
            document.add(new Paragraph("Shipping Address: " + shippingAddress, infoFont));
            document.add(Chunk.NEWLINE);

            //Table for items
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 2f, 2f, 2f});

            addTableHeader(table);
            addRows(table, order.getOrderItems());

            document.add(table);
            document.add(Chunk.NEWLINE);

            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph total = new Paragraph("Total Amount: ₹" + order.getTotalAmount(), totalFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate invoice pdf!");
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Product", "Price", "Quantity", "Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPhrase(new Phrase(columnTitle, headFont));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            table.addCell(item.getProduct().getName());
            table.addCell("₹" + item.getPrice());
            table.addCell(String.valueOf(item.getQuantity()));
            BigDecimal total = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            table.addCell("₹" + total);
        }
    }
}