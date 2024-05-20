package practicasistemas;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class GeneratePDF {

    public static void main(String[] args) throws IOException {
        boolean descuento = false;
        String dest = "generated_invoice.pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        String imagen = "resources/imgEmpresa.jpg";

        float[] columnDetWidths = {1, 1}; // Two columns
        Table detailsTable = new Table(UnitValue.createPercentArray(columnDetWidths));
        detailsTable.setWidth(UnitValue.createPercentValue(100));

        // Company details
        Cell companyDetailsCell = new Cell()
                .add(new Paragraph("Astorga").setTextAlignment(TextAlignment.CENTER).setMarginTop(5))
                .add(new Paragraph("P24001017F").setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("Calle de la Iglesia, 13").setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("24280 Astorga León").setTextAlignment(TextAlignment.CENTER).setMarginBottom(5))
                .setBorder(new SolidBorder(1));
        detailsTable.addCell(companyDetailsCell);

        // Invoice details
        Cell invoiceDetailsCell = new Cell()
                .add(new Paragraph("IBAN: GR9420125003305201112544").setTextAlignment(TextAlignment.RIGHT).setMarginTop(5).setMarginRight(5))
                .add(new Paragraph("Fecha de alta: 22/03/2020").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("Tipo de cálculo: Hogar").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .setBorder(Border.NO_BORDER);
        detailsTable.addCell(invoiceDetailsCell);

        document.add(detailsTable);
        
        float[] columnImgWidths = {1, 0.975f}; // Two columns
        Table imgDetTable = new Table(UnitValue.createPercentArray(columnImgWidths));
        imgDetTable.setWidth(UnitValue.createPercentValue(100));
        
        // Image part
        Cell imageCell = new Cell()
                .add(new Image(ImageDataFactory.create(imagen)).setHorizontalAlignment(HorizontalAlignment.CENTER))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        imgDetTable.addCell(imageCell);

        // Recipient details
        Cell recipientDetailsCell = new Cell()
                .add(new Paragraph("Destinatario:").setBold().setMarginTop(5).setMarginLeft(5))
                .add(new Paragraph("Vittorio Diez Otero").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("DNI: 00538394X").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("IGLESIA (LA)0003").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("Astorga").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5).setMarginBottom(5))
                .setBorder(new SolidBorder(1));
        imgDetTable.addCell(recipientDetailsCell);
        
        document.add(imgDetTable);
        
        // Lecturas del medidor
        float[] columnMeterWidths = {1, 1, 1}; // Tres columnas iguales
        Table meterTable = new Table(UnitValue.createPercentArray(columnMeterWidths));
        meterTable.setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10) // Márgenes superior
                .setMarginBottom(10) // Márgenes inferior
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

        // Agregar las celdas de lectura de medidor
        meterTable.addCell(new Cell().add(new Paragraph("Lectura actual: 637")).setBorder(Border.NO_BORDER));
        meterTable.addCell(new Cell().add(new Paragraph("Lectura anterior: 586")).setBorder(Border.NO_BORDER));
        meterTable.addCell(new Cell().add(new Paragraph("Consumo: 51 metros cúbicos.")).setBorder(Border.NO_BORDER));

        // Agregar la tabla al documento
        document.add(meterTable);

        // Add invoice period
        document.add(new Paragraph("Recibo agua: Primer trimestre de 2023").setBold().setItalic().setTextAlignment(TextAlignment.CENTER).setMarginTop(30).setMarginBottom(30));
        

        if(descuento){
            float[] columnWidths = {3, 3, 3, 3, 3, 3, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Add table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Concepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Subconcepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("M3 incluídos")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("B.Imponible")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("IVA %")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Importe")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Descuento")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));

            // Add table data
            String[][] data = {
                    {"Agua", "Fijo", "30,00", "15,00", "21,00%", "03,15", "hola"},
                    {"Agua", "Primer tramo", "20,00", "03,00", "21,00%", "00,63", "hola"},
                    {"Agua", "Segundo tramo", "01,00", "00,22", "21,00%", "00,05", "hola"},
                    {"Agua", "Tercer tramo", "00,00", "00,00", "21,00%", "00,00", "hola"},
                    {"Agua", "Cuarto tramo", "00,00", "00,00", "21,00%", "00,00", "hola"},
                    {"Desagüe", "Desagüe", "00,00", "01,82", "00,00%", "00,00", "hola"},
                    {"Alcantarillado", "Fijo", "00,00", "00,18", "10,00%", "00,02", "hola"}
            };

            for (String[] row : data) {
                for (String cell : row) {
                    table.addCell(new Cell().add(new Paragraph(cell)).setMargin(5));
                }
            }

            // Add totals row
            table.addCell(new Cell(1, 3).add(new Paragraph("TOTALES")).setMargin(5));
            table.addCell(new Cell().add(new Paragraph("20,23")).setMargin(5));
            table.addCell(new Cell(5, 7).add(new Paragraph("03,85")).setMargin(5).setTextAlignment(TextAlignment.CENTER));

            document.add(table);
        }else{
            // Create a table
            float[] columnWidths = {3, 3, 3, 3, 3, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Add table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Concepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Subconcepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("M3 incluídos")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("B.Imponible")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("IVA %")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Importe")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));

            // Add table data
            String[][] data = {
                    {"Agua", "Fijo", "30,00", "15,00", "21,00%", "03,15"},
                    {"Agua", "Primer tramo", "20,00", "03,00", "21,00%", "00,63"},
                    {"Agua", "Segundo tramo", "01,00", "00,22", "21,00%", "00,05"},
                    {"Agua", "Tercer tramo", "00,00", "00,00", "21,00%", "00,00"},
                    {"Agua", "Cuarto tramo", "00,00", "00,00", "21,00%", "00,00"},
                    {"Desagüe", "Desagüe", "00,00", "01,82", "00,00%", "00,00"},
                    {"Alcantarillado", "Fijo", "00,00", "00,18", "10,00%", "00,02"}
            };

            for (String[] row : data) {
                for (String cell : row) {
                    table.addCell(new Cell().add(new Paragraph(cell)).setMargin(5));
                }
            }

            // Add totals row
            table.addCell(new Cell(1, 3).add(new Paragraph("TOTALES")).setMargin(5));
            table.addCell(new Cell().add(new Paragraph("20,23")).setMargin(5));
            table.addCell(new Cell(5, 6).add(new Paragraph("03,85")).setMargin(5).setTextAlignment(TextAlignment.CENTER));

            document.add(table);
        }
        float[] finalPriceWidths = {1, 1}; // Two columns
        Table finalPriceTable = new Table(UnitValue.createPercentArray(finalPriceWidths));
        finalPriceTable.setWidth(UnitValue.createPercentValue(100));

        // Company details
        Cell totalCell = new Cell()
                .add(new Paragraph("TOTAL BASE IMPONIBLE").setMarginTop(30))
                .add(new Paragraph("TOTAL IVA").setMarginBottom(10))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(2));
        finalPriceTable.addCell(totalCell);

        // Invoice details
        Cell priceCell = new Cell()
                .add(new Paragraph("20,23").setTextAlignment(TextAlignment.RIGHT).setMarginTop(30))
                .add(new Paragraph("03,85").setTextAlignment(TextAlignment.RIGHT).setMarginBottom(10))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(2));
        finalPriceTable.addCell(priceCell);

        document.add(finalPriceTable);

        float[] finalWidths = {1, 1}; // Two columns
        Table finalTable = new Table(UnitValue.createPercentArray(finalWidths));
        finalTable.setWidth(UnitValue.createPercentValue(100));

        // Company details
        Cell totalFinalCell = new Cell()
                .add(new Paragraph("TOTAL RECIBO"))
                .setBorder(Border.NO_BORDER);
        finalTable.addCell(totalFinalCell);

        // Invoice details
        Cell priceFinalCell = new Cell()
                .add(new Paragraph("24,08").setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER);
        finalTable.addCell(priceFinalCell);

        document.add(finalTable);
        
        document.close();
    }
}