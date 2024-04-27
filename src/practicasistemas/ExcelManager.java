/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 0@author Guille & Ovi 😎
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
/**
 *
 * @author Guille
 */
public class ExcelManager {

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
        return contribuyente;
    }

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
}
