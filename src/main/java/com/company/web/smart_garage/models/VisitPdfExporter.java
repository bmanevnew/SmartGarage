package com.company.web.smart_garage.models;

import com.company.web.smart_garage.data_transfer_objects.CurrencyExportDTO;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.Set;

public class VisitPdfExporter {
    //  private final CurrencyExportDTO currencyExportDTO;
    private final Visit visit;
    //TODO
    private final Double rate;

    public VisitPdfExporter(CurrencyExportDTO currencyExportDTO, Visit visit, Double rate) {
        //  this.currencyExportDTO = currencyExportDTO;
        this.visit = visit;
        this.rate = rate;
    }

    public VisitPdfExporter(Visit visit, Double rate) {
        this.visit = visit;
        this.rate = rate;
    }

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

    private void writeUserData(PdfPTable table) {
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

    private void writeServicesData(PdfPTable table) {
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

        for (Repair repair : repairs) {
            borderlessCell.setPhrase(new Phrase(repair.getName()));
            table.addCell(borderlessCell);
            borderlessCell.setPhrase(new Phrase(String.valueOf(String.format("%.2f", repair.getPrice() * rate))));
            table.addCell(borderlessCell);
        }
        borderlessCell.setPhrase(new Phrase());
        table.addCell(borderlessCell);
        table.addCell(borderlessCell);
        table.addCell(borderlessCell);

        borderlessCell.setPhrase(new Phrase("Total: " +
                //  currencyExportDTO.getCurrency().toString() + " " +
                String.format("%.2f", visit.getTotalCost() * rate),
                fontBold));
        table.addCell(borderlessCell);
    }

    public ByteArrayOutputStream export() throws DocumentException {
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
        writeUserData(table);

        PdfPTable table2 = new PdfPTable(2);
        table2.setWidthPercentage(100f);

        writeServicesData(table2);

        document.add(table);
        document.add(line);

        document.add(table2);

        document.close();

        return baos;
    }

}