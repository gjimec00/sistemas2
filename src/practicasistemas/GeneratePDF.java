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

    /**
     * Genera los PDF de todos los contribuyentes de la lista dada.
     *
     * @param contribuyente Contribuyente objetivo a generar pdf.
     * @param datosRecibo Datos del recibo para generar la tabla correspondiente.
     * @param costeTotal Coste total del recibo.
     * @param ivaTotal Iva total del recibo.
     * @param tipoCalculo Tipo de cálculo realizado en el recibo correspondiente.
     * @param input Input recibido del usuario con el trimestre correspondiente.
     *
     * @throws IOException Excepción producida en caso de que el parámetro "input" fuese incorrecto.
     */
    
    public static void generarPDFs(Contribuyente contribuyente, ArrayList<ArrayList<String>> datosRecibo, double costeTotal, double ivaTotal, String tipoCalculo, String input) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("./resources/recibos/").append(contribuyente.getNifnie()).append(contribuyente.getNombre()).append(contribuyente.getApellido1()).append(contribuyente.getApellido2()).append(".pdf");
        String dest = sb.toString();
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        String imagen = "resources/imgEmpresa.jpg";

        float[] columnDetWidths = {1, 1};
        Table detailsTable = new Table(UnitValue.createPercentArray(columnDetWidths));
        detailsTable.setWidth(UnitValue.createPercentValue(100));

        Cell companyDetailsCell = new Cell()
                .add(new Paragraph("Astorga").setTextAlignment(TextAlignment.CENTER).setMarginTop(5))
                .add(new Paragraph("P24001017F").setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("Calle de la Iglesia, 13").setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("24280 Astorga León").setTextAlignment(TextAlignment.CENTER).setMarginBottom(5))
                .setBorder(new SolidBorder(1));
        detailsTable.addCell(companyDetailsCell);

        Cell invoiceDetailsCell = new Cell()
                .add(new Paragraph("IBAN: "+ contribuyente.getIban()).setTextAlignment(TextAlignment.RIGHT).setMarginTop(5).setMarginRight(5))
                .add(new Paragraph("Tipo de cálculo: " + tipoCalculo).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("Fecha de alta: " + date.format(contribuyente.getFechaAlta())).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .setBorder(Border.NO_BORDER);
        detailsTable.addCell(invoiceDetailsCell);

        document.add(detailsTable);
        
        float[] columnImgWidths = {1, 0.975f};
        Table imgDetTable = new Table(UnitValue.createPercentArray(columnImgWidths));
        imgDetTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell imageCell = new Cell()
                .add(new Image(ImageDataFactory.create(imagen)).setHorizontalAlignment(HorizontalAlignment.CENTER))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        imgDetTable.addCell(imageCell);

        Cell recipientDetailsCell = new Cell()
                .add(new Paragraph("Destinatario:").setBold().setMarginTop(5).setMarginLeft(5))
                .add(new Paragraph(contribuyente.getNombre() + " " + contribuyente.getApellido1() + " " + contribuyente.getApellido2()).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("DNI: " + contribuyente.getNifnie()).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph(contribuyente.getDireccion() + " " + contribuyente.getNumero()).setTextAlignment(TextAlignment.RIGHT).setMarginRight(5))
                .add(new Paragraph("Astorga").setTextAlignment(TextAlignment.RIGHT).setMarginRight(5).setMarginBottom(5))
                .setBorder(new SolidBorder(1));
        imgDetTable.addCell(recipientDetailsCell);
        
        document.add(imgDetTable);
        
        float[] columnMeterWidths = {1, 1, 1};
        Table meterTable = new Table(UnitValue.createPercentArray(columnMeterWidths));
        meterTable.setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10)
                .setMarginBottom(10)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

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
        document.add(new Paragraph("Recibo agua: " + trimestreT + " de " + Integer.toString(year)).setBold().setItalic().setTextAlignment(TextAlignment.CENTER).setMarginTop(30).setMarginBottom(30));
        

        if(contribuyente.getBonificacion() != 0){
            float[] columnWidths = {3, 3, 3, 3, 3, 3, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Concepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Subconcepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("M3 incluídos")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("B.Imponible")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("IVA %")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Importe")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Descuento")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));

            int rows = datosRecibo.size();
            int cols = datosRecibo.get(0).size();
            String[][] array = new String[rows][];
            for (int i = 0; i < rows; i++) {
                ArrayList<String> sublist = datosRecibo.get(i);
                array[i] = sublist.toArray(new String[0]);
            }
            
            String[][] data = array;
            for (String[] row : data) {
                for (String cell : row) {
                    table.addCell(new Cell().add(new Paragraph(cell)).setMargin(5));
                }
            }

            table.addCell(new Cell(1, 3).add(new Paragraph("TOTALES")).setMargin(5));
            table.addCell(new Cell().add(new Paragraph(df.format(contribuyente.getBaseImponible())).setMargin(5)));
            table.addCell(new Cell(5, 7).add(new Paragraph(df.format(ivaTotal)).setMargin(5).setTextAlignment(TextAlignment.CENTER)));

            document.add(table);
        }else{
            float[] columnWidths = {3, 3, 3, 3, 3, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            table.addHeaderCell(new Cell().add(new Paragraph("Concepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Subconcepto")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("M3 incluídos")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("B.Imponible")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("IVA %")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));
            table.addHeaderCell(new Cell().add(new Paragraph("Importe")).setBackgroundColor(new DeviceRgb(224, 224, 224)).setMargin(5));

            int rows = datosRecibo.size();
            int cols = datosRecibo.get(0).size();
            String[][] array = new String[rows][];
            for (int i = 0; i < rows; i++) {
                ArrayList<String> sublist = datosRecibo.get(i);
                array[i] = sublist.toArray(new String[0]);
            }
            
            String[][] data = array;
            for (String[] row : data) {
                for (String cell : row) {
                    table.addCell(new Cell().add(new Paragraph(cell)).setMargin(5));
                }
            }

            table.addCell(new Cell(1, 3).add(new Paragraph("TOTALES")).setMargin(5));
            table.addCell(new Cell().add(new Paragraph(df.format(contribuyente.getBaseImponible()))).setMargin(5));
            table.addCell(new Cell(5, 6).add(new Paragraph(df.format(ivaTotal))).setMargin(5).setTextAlignment(TextAlignment.CENTER));

            document.add(table);
        }
        
        float[] finalPriceWidths = {1, 1};
        Table finalPriceTable = new Table(UnitValue.createPercentArray(finalPriceWidths));
        finalPriceTable.setWidth(UnitValue.createPercentValue(100));

        Cell totalCell = new Cell()
                .add(new Paragraph("TOTAL BASE IMPONIBLE").setMarginTop(30))
                .add(new Paragraph("TOTAL IVA").setMarginBottom(10))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(2));
        finalPriceTable.addCell(totalCell);

        Cell priceCell = new Cell()
                .add(new Paragraph(df.format(contribuyente.getBaseImponible())).setTextAlignment(TextAlignment.RIGHT).setMarginTop(30))
                .add(new Paragraph(df.format(ivaTotal)).setTextAlignment(TextAlignment.RIGHT).setMarginBottom(10))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(2));
        finalPriceTable.addCell(priceCell);

        document.add(finalPriceTable);

        float[] finalWidths = {1, 1};
        Table finalTable = new Table(UnitValue.createPercentArray(finalWidths));
        finalTable.setWidth(UnitValue.createPercentValue(100));

        Cell totalFinalCell = new Cell()
                .add(new Paragraph("TOTAL RECIBO"))
                .setBorder(Border.NO_BORDER);
        finalTable.addCell(totalFinalCell);

        Cell priceFinalCell = new Cell()
                .add(new Paragraph(df.format(costeTotal)).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER);
        finalTable.addCell(priceFinalCell);

        document.add(finalTable);
        
        document.close();
    }

    /**
     * Genera el PDF con un resumen de los totales de un trimestre dado.
     *
     * @param totalBaseImponible Base imponible total del resumen.
     * @param totalIva Iva total del recibo.
     * @param input Input recibido del usuario con el trimestre correspondiente.
     *
     * @throws IOException Excepción producida en caso de que el parámetro "input" fuese incorrecto.
     */
    
    public static void generarResumen(double totalBaseImponible, double totalIva, String input) throws IOException{
        String dest = "resources/recibos/resumen.pdf";
        DecimalFormat df = new DecimalFormat("0.00");
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        double recibos = totalBaseImponible + totalIva;
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

        float[] tableWidth = {1};
        Table table = new Table(UnitValue.createPercentArray(tableWidth));
        table.setWidth(UnitValue.createPercentValue(100));

        Cell cell = new Cell()
            .add(new Paragraph("RESUMEN PADRON DE AGUA " + trimestreT + " de " + Integer.toString(year)))
            .add(new Paragraph("TOTAL BASE IMPONIBLE................." + df.format(totalBaseImponible)))
            .add(new Paragraph("TOTAL IVA......................................... " + df.format(totalIva)))
            .add(new Paragraph("TOTAL RECIBOS.............................. " + df.format(recibos)))
            .setBorder(new SolidBorder(1));
        table.addCell(cell);

        document.add(table);

        document.close();
    }
}
