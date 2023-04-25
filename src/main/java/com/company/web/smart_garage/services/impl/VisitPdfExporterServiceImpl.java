package com.company.web.smart_garage.services.impl;

import com.company.web.smart_garage.exceptions.InvalidParamException;
import com.company.web.smart_garage.models.Repair;
import com.company.web.smart_garage.models.Visit;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.posadskiy.currencyconverter.CurrencyConverter;
import com.posadskiy.currencyconverter.enums.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class VisitPdfExporterServiceImpl implements com.company.web.smart_garage.services.VisitPdfExporterService {

    private final CurrencyConverter converter;

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        // cell.setBackgroundColor();
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.BLACK);

        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA);
        fontBold.setColor(Color.BLACK);
        fontBold.setStyle(Font.BOLD);

        cell.setPhrase(new Phrase("Visit ID:", fontBold));
        table.addCell(cell);

        cell.setPhrase(new Phrase("User details:", fontBold));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Vehicle details:", fontBold));
        table.addCell(cell);
    }

    private void writeUserData(Visit visit, PdfPTable table) {
        PdfPCell firstCell = new PdfPCell(new Phrase(String.valueOf(visit.getId())));
        firstCell.setBorder(Rectangle.NO_BORDER);
        firstCell.setRowspan(3);
        PdfPCell otherCell = new PdfPCell();
        otherCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(firstCell);
        otherCell.setPhrase(new Phrase(visit.getVisitor().getUsername()));
        table.addCell(otherCell);
        otherCell.setPhrase(new Phrase(visit.getVehicle().getBrand()));
        table.addCell(otherCell);
        otherCell.setPhrase(new Phrase(visit.getVisitor().getEmail()));
        table.addCell(otherCell);
        otherCell.setPhrase(new Phrase(visit.getVehicle().getModel()));
        table.addCell(otherCell);
        otherCell.setPhrase(new Phrase(visit.getVisitor().getPhoneNumber()));
        table.addCell(otherCell);
        otherCell.setPhrase(new Phrase(visit.getVehicle().getLicensePlate()));
        table.addCell(otherCell);
    }

    private void writeServicesData(Visit visit, String currencyCode, PdfPTable table) {
        PdfPCell borderlessCell = new PdfPCell();
        borderlessCell.setBorder(Rectangle.NO_BORDER);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.BLACK);

        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA);
        fontBold.setColor(Color.BLACK);
        fontBold.setStyle(Font.BOLD);

        Set<Repair> repairs = visit.getRepairs();

        borderlessCell.setPhrase(new Phrase("Service name:", fontBold));
        table.addCell(borderlessCell);
        borderlessCell.setPhrase(new Phrase("Price:", fontBold));
        table.addCell(borderlessCell);

        double rate = converter.rate(Currency.BGN, Currency.findByCode(currencyCode)
                .orElseThrow(() -> new InvalidParamException("Currency not supported.")));

        for (Repair repair : repairs) {
            borderlessCell.setPhrase(new Phrase(repair.getName()));
            table.addCell(borderlessCell);
            borderlessCell.setPhrase(new Phrase(String.valueOf(
                    String.format("%.2f %s", repair.getPrice() * rate, currencyCode))));
            table.addCell(borderlessCell);
        }
        borderlessCell.setPhrase(new Phrase());
        table.addCell(borderlessCell);
        table.addCell(borderlessCell);
        table.addCell(borderlessCell);

        borderlessCell.setPhrase(new Phrase("Total: " +
                String.format("%.2f %s", visit.getTotalCost() * rate, currencyCode),
                fontBold));
        table.addCell(borderlessCell);
    }

    @Override
    public ByteArrayOutputStream export(Visit visit, String currencyCode) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        Chunk line = new Chunk(new LineSeparator());

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Smart Garage", font);
        p.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(p);
        document.add(line);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 3.5f, 3.0f});

        writeTableHeader(table);
        writeUserData(visit, table);

        PdfPTable table2 = new PdfPTable(2);
        table2.setWidthPercentage(100f);

        writeServicesData(visit, currencyCode, table2);

        document.add(table);
        document.add(line);

        document.add(table2);

        document.close();

        return baos;
    }

}