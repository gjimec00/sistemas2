/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import java.util.Map;
import java.util.LinkedHashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import POJOS.Contribuyente;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import POJOS.Ordenanza;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author Guille
 */
public class ExcelManager {

    /**
     * Genera un mapa con los contribuyentes existentes en el excel.
     *
     * @return Mapa con los contribuyentes.
     * @param excel Excel dado para substraer datos.
     */    
    
    public static Map getContribuyentesExcel(String excel){
        Map<Integer, Contribuyente> mapContribuyentes = new LinkedHashMap<>();
        try (FileInputStream inputExcel = new FileInputStream(excel);
            Workbook workbook = new XSSFWorkbook(inputExcel)){
            Sheet sheet = workbook.getSheetAt(0);
            for(int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row != null && row.getCell(0) != null){
                    Contribuyente contribuyente = createContribuyenteFromRow(row);
                    mapContribuyentes.put(contribuyente.getIdContribuyente(), contribuyente);
                }else{
                    Contribuyente contribuyente = new Contribuyente();
                    contribuyente.setIdContribuyente(0);
                    mapContribuyentes.put(i + 1, contribuyente);
                }
            }
            workbook.close();
            inputExcel.close();
        }catch (Exception e){
            Logger.getLogger(ExcelManager.class.getName()).log(Level.SEVERE, "Error al leer el archivo Excel", e);
        }
        return mapContribuyentes;
    }

    /**
     * Genera contribuyentes a partir de una columna dada.
     *
     * @return Contribuyente.
     * @param row Columna con la información perteneciente al contribuyente.
     */
    
    private static Contribuyente createContribuyenteFromRow(Row row){
        int id = row.getRowNum() + 1;
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setIdContribuyente(id);
        contribuyente.setNombre(row.getCell(0).toString());

        if(row.getCell(3) != null){
            contribuyente.setNifnie(row.getCell(3).toString());
        }
        if(row.getCell(1) != null){
            contribuyente.setApellido1(row.getCell(1).toString());
        }
        if(row.getCell(2) != null){
            contribuyente.setApellido2(row.getCell(2).toString());
        }
        if(row.getCell(4) != null){
            contribuyente.setDireccion(row.getCell(4).toString());
        }
        if(row.getCell(5) != null){
            contribuyente.setNumero(row.getCell(5).toString());
        }
        if(row.getCell(6) != null){
            contribuyente.setPaisCcc(row.getCell(6).toString());
        }
        if(row.getCell(7) != null){
            contribuyente.setCcc(row.getCell(7).toString());
        } 
        if(row.getCell(10) != null){
            contribuyente.setExencion(row.getCell(10).toString().charAt(0));
        }
        if(row.getCell(11) != null){
            contribuyente.setBonificacion(Double.parseDouble(row.getCell(11).toString()));
        }
        Set lecturas = new HashSet();
        if(row.getCell(12) != null){
            lecturas.add(row.getCell(12).toString());
        }
        if(row.getCell(13) != null){
            lecturas.add(row.getCell(13).toString());
        }
        contribuyente.setLecturases(lecturas);
        if(row.getCell(14) != null){
            contribuyente.setFechaAlta(row.getCell(14).getDateCellValue());
        }
        if(row.getCell(15) != null){
            contribuyente.setFechaBaja(row.getCell(15).getDateCellValue());
        }
        if(row.getCell(16) != null){
            String conceptos = row.getCell(16).toString();
            String[] subcadenas = conceptos.split("\\s+");
            Set ordenanzas = new HashSet();
            for(String subcadena : subcadenas){
                ordenanzas.add(subcadena);
            }
            ordenanzas.remove(' ');
            contribuyente.setRelContribuyenteOrdenanzas(ordenanzas);
        }
        return contribuyente;
    }

    /**
     * Escribe en el excel el contribuyente nuevo en caso de ser subsanado por su NIFNIE.
     *
     * @param archivo Pathfile al excel objetivo.
     * @param contribuyente Contribuyente subsanado por su NIFNIE.
     */
    
    public static void setNewContribuyente(String archivo, Contribuyente contribuyente){
        try {
            FileInputStream inputExcel = new FileInputStream(new File(archivo));
            Workbook workbook = new XSSFWorkbook(inputExcel);
            Sheet sheet = workbook.getSheetAt(0);

            for(int i = 1; i < sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row != null){
                    if(contribuyente.getIdContribuyente() == row.getRowNum()+1){
                        Cell cell = row.getCell(3);
                        cell.setCellValue(contribuyente.getNifnie());
                    }
                }
            }
            
            FileOutputStream fileOutput = null;
            File file;

            file = new File(archivo);
            fileOutput = new FileOutputStream(file);

            workbook.write(fileOutput);
            workbook.close();

            fileOutput.close();
        } catch (Exception e){
        
        }
    }

    /**
     * Escribe en el excel el contribuyente nuevo en caso de ser subsanado por su CCC o IBAN.
     *
     * @param archivo Pathfile al excel objetivo.
     * @param contribuyente Contribuyente subsanado por su CCC o IBAN.
     */
    
    public static void setNewCCCIBAN(String archivo, Contribuyente contribuyente){
        try {
            FileInputStream inputExcel = new FileInputStream(new File(archivo));
            Workbook workbook = new XSSFWorkbook(inputExcel);
            Sheet sheet = workbook.getSheetAt(0);

            for(int i = 1; i < sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row != null){
                    if(contribuyente.getIdContribuyente() == row.getRowNum()+1){
                        Cell cellCCC = row.getCell(7);
                        cellCCC.setCellValue(contribuyente.getCcc());
                        Cell cellIBAN = row.createCell(8);
                        cellIBAN.setCellValue(contribuyente.getIban());
                    }
                }
            }
            
            FileOutputStream fileOutput = null;
            File file;

            file = new File(archivo);
            fileOutput = new FileOutputStream(file);

            workbook.write(fileOutput);
            workbook.close();

            fileOutput.close();
        } catch (Exception e){
        
        }
    }

    /**
     * Obtiene un mapa con las ordenanzas dadas en el excel.
     *
     * @param excel Pathfile al excel objetivo.
     * @return Mapa con las ordenanzas.
     */
    
    public static Map getOrdenanzasExcel(String excel){
        Map<Integer, Ordenanza> mapOrdenanzas = new LinkedHashMap<>();
        try (FileInputStream inputExcel = new FileInputStream(excel);
            Workbook workbook = new XSSFWorkbook(inputExcel)){
            Sheet sheet = workbook.getSheetAt(1);
            for(int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row != null && row.getCell(0) != null){
                    Ordenanza ordenanza = createOrdenanzaFromRow(row);
                    mapOrdenanzas.put(ordenanza.getId(), ordenanza);
                }
            }
            workbook.close();
            inputExcel.close();
        }catch (Exception e){
            Logger.getLogger(ExcelManager.class.getName()).log(Level.SEVERE, "Error al leer el archivo Excel", e);
        }
        return mapOrdenanzas;
    }

    /**
     * Crea un objeto ordenanza a partir de una fila dada.
     *
     * @param row Fila del excel con los datos necesarios.
     * @return Objeto ordenanza.
     */

    private static Ordenanza createOrdenanzaFromRow(Row row){
        int id = row.getRowNum() + 1;
        Ordenanza ordenanza = new Ordenanza();
        ordenanza.setId(id);
        ordenanza.setIdOrdenanza((int) Double.parseDouble(row.getCell(2).toString()));
        
        if(row.getCell(0) != null){
            ordenanza.setPueblo(row.getCell(0).toString());
        }
        if(row.getCell(1) != null){
            ordenanza.setTipoCalculo(row.getCell(1).toString());
        }
        if(row.getCell(3) != null){
            ordenanza.setConcepto(row.getCell(3).toString());
        }
        if(row.getCell(4) != null){
            ordenanza.setSubconcepto(row.getCell(4).toString());
        }
        if(row.getCell(5) != null){
            ordenanza.setDescripcion(row.getCell(5).toString());
        }
        if(row.getCell(6) != null){
            ordenanza.setAcumulable(row.getCell(6).toString());
        }
        if(row.getCell(7) != null){
            ordenanza.setPrecioFijo((int) Double.parseDouble(row.getCell(7).toString()));
        } 
        if(row.getCell(8) != null){
            ordenanza.setM3incluidos((int) Double.parseDouble(row.getCell(8).toString()));
        } 
        if(row.getCell(9) != null){
            ordenanza.setPreciom3(Double.parseDouble(row.getCell(9).toString()));
        } 
        if(row.getCell(10) != null){
            ordenanza.setPorcentaje(Double.parseDouble(row.getCell(10).toString()));
        } 
        if(row.getCell(11) != null){
            ordenanza.setConceptoRelacionado((int) Double.parseDouble(row.getCell(11).toString()));
        } 
        if(row.getCell(12) != null){
            ordenanza.setIva(Double.parseDouble(row.getCell(12).toString()));
        } 
        return ordenanza;
    }
}
