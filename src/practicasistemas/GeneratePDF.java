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
import POJOS.Contribuyente;
import POJOS.Ordenanza;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;

public class GeneratePDF {

    public static void generarPDFs(Contribuyente contribuyente, ArrayList<ArrayList<String>> datosRecibo, double costeTotal, double ivaTotal, String tipoCalculo, String input) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("./resources/recibos/").append(contribuyente.getNifnie()).append(contribuyente.getNombre()).append(contribuyente.getApellido1()).append(contribuyente.getApellido2()).append(".pdf");
        String dest = sb.toString();
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
                .add(new Paragraph("IBAN: "+ contribuyente.getIban()).setTextAlignment(TextAlignment.RIGHT).setMarginTop(5).setMarginRight(5))
                .add(new Paragraph("Tipo de cálculo: " + tipoCalculo).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("Fecha de alta: " + fecha.format(contribuyente.getFechaAlta())).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
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
                .add(new Paragraph(contribuyente.getNombre() + " " + contribuyente.getApellido1() + " " + contribuyente.getApellido2()).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("DNI: " + contribuyente.getNifnie()).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph(contribuyente.getDireccion() + " " + contribuyente.getNumero()).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
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
        List < String > lecturas = new ArrayList < > (contribuyente.getLecturases());
        if (lecturas.size() == 1) {
            lecturas.add(lecturas.get(0));
        }
        if (Double.parseDouble(lecturas.get(1)) < Double.parseDouble(lecturas.get(0))) {
            String aux = lecturas.get(0);
            lecturas.set(0, lecturas.get(1));
            lecturas.set(1, aux);
        }
        double consumo = Double.parseDouble(lecturas.get(1)) - Double.parseDouble(lecturas.get(0));
        meterTable.addCell(new Cell().add(new Paragraph("Lectura actual: " + lecturas.get(1))).setBorder(Border.NO_BORDER));
        meterTable.addCell(new Cell().add(new Paragraph("Lectura anterior: " + lecturas.get(0))).setBorder(Border.NO_BORDER));
        meterTable.addCell(new Cell().add(new Paragraph("Consumo: " + consumo + " metros cúbicos.")).setBorder(Border.NO_BORDER));

        // Agregar la tabla al documento
        document.add(meterTable);
        String trimestre = input.substring(0, 2);
        int year = Integer.parseInt(input.substring(3));
        String trimestreT;
        if (trimestre.equals("1T")) {
            trimestreT = "Primer trimestre";
        } else if (trimestre.equals("2T")) {
            trimestreT = "Segundo trimestre";
        } else if (trimestre.equals("3T")) {
            trimestreT = "Tercer trimestre";
        } else {
            trimestreT = "Cuarto trimestre";
        }
        // Add invoice period
        document.add(new Paragraph("Recibo agua: " + trimestreT + " de " + Integer.toString(year)).setBold().setItalic().setTextAlignment(TextAlignment.CENTER).setMarginTop(30).setMarginBottom(30));
        

        if(contribuyente.getBonificacion() != 0){
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
            int rows = datosRecibo.size();
            int cols = datosRecibo.get(0).size();
            String[][] array = new String[rows][];
            for (int i = 0; i < rows; i++) {
                ArrayList<String> sublist = datosRecibo.get(i);
                array[i] = sublist.toArray(new String[0]); // Convertir la sublista a array y asignarla
            }
            String[][] data = array;

            for (String[] row : data) {
                for (String cell : row) {
                    table.addCell(new Cell().add(new Paragraph(cell)).setMargin(5));
                }
            }

            // Add totals row
            table.addCell(new Cell(1, 3).add(new Paragraph("TOTALES")).setMargin(5));
            table.addCell(new Cell().add(new Paragraph(df.format(costeTotal)).setMargin(5)));
            table.addCell(new Cell(5, 7).add(new Paragraph(df.format(ivaTotal)).setMargin(5).setTextAlignment(TextAlignment.CENTER)));

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
            table.addCell(new Cell().add(new Paragraph(df.format(costeTotal))).setMargin(5));
            table.addCell(new Cell(5, 6).add(new Paragraph(df.format(ivaTotal))).setMargin(5).setTextAlignment(TextAlignment.CENTER));

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
                .add(new Paragraph(df.format(costeTotal)).setTextAlignment(TextAlignment.RIGHT).setMarginTop(30))
                .add(new Paragraph(df.format(ivaTotal)).setTextAlignment(TextAlignment.RIGHT).setMarginBottom(10))
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
        double totalRec = costeTotal + ivaTotal;
        Cell priceFinalCell = new Cell()
                .add(new Paragraph(df.format(totalRec)).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER);
        finalTable.addCell(priceFinalCell);

        document.add(finalTable);
        
        document.close();
    }
}