// PDFGenerator.java
package com.local.train.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.local.train.entity.Booking;
import com.local.train.entity.Passenger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PDFGenerator {

    public byte[] generateTicketPdf(Booking booking, byte[] qrCode) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Title
            document.add(new Paragraph("LOCAL TRAIN E-TICKET")
                    .setBold().setFontSize(22).setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // Main Info Table
            Table mainInfo = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            mainInfo.addCell(new Paragraph("PNR: " + booking.getPnrNumber()).setBold());
            mainInfo.addCell(new Paragraph("Status: " + booking.getStatus()).setBold());
            mainInfo.addCell("Date: " + booking.getJourneyDate().toLocalDate());
            mainInfo.addCell("Class: " + booking.getTravelClass());
            mainInfo.addCell("From: " + booking.getSchedule().getSourceStation().getStationName());
            mainInfo.addCell("To: " + booking.getSchedule().getDestinationStation().getStationName());
            document.add(mainInfo);

            // Add QR Code
            if (qrCode != null) {
                Image image = new Image(ImageDataFactory.create(qrCode));
                image.setWidth(100);
                image.setHeight(100);
                image.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                document.add(new Paragraph("\n"));
                document.add(image);
                document.add(new Paragraph("Scan for Verification")
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(8));
            }

            // Passenger Details
            document.add(new Paragraph("\nPassenger Details:").setBold());
            Table passengerTable = new Table(UnitValue.createPercentArray(new float[]{40, 30, 30})).useAllAvailableWidth();
            passengerTable.addHeaderCell("Name");
            passengerTable.addHeaderCell("Age");
            passengerTable.addHeaderCell("Gender");

            for (Passenger passenger : booking.getPassengers()) {
                passengerTable.addCell(passenger.getName());
                passengerTable.addCell(String.valueOf(passenger.getAge()));
                passengerTable.addCell(passenger.getGender());
            }
            document.add(passengerTable);

            // Fare Details
            document.add(new Paragraph("\nFare Details:").setBold());
            document.add(new Paragraph("Total Fare: ₹" + String.format("%.2f", booking.getTotalFare())));

            // Footer
            document.add(new Paragraph("\n\nImportant Instructions:")
                    .setFontSize(10).setItalic());
            document.add(new Paragraph("1. This is a computer-generated ticket and does not require a physical signature.")
                    .setFontSize(9));
            document.add(new Paragraph("2. Please carry a valid original Photo ID proof during the journey.")
                    .setFontSize(9));
            document.add(new Paragraph("3. Cancellation is allowed only up to 3 hours before the scheduled journey.")
                    .setFontSize(9));

            document.close();
            return baos.toByteArray();
        }
    }
}
