/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import POJOS.Contribuyente;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Guille & Ovi 😎
 */
public class Practicasistemas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Map<Integer, Contribuyente> contribuyentes = new LinkedHashMap<>();
        contribuyentes = ExcelManager.getContribuyentesExcel("./resources/SistemasAgua.xlsx");
        for(int i = 1; i < contribuyentes.size(); i++){
            if(contribuyentes.get(i).getIdContribuyente() != 0){
            String nifnie = contribuyentes.get(i).getNifnie();
            comprobarDNI(nifnie);
            }
            
        }
    }
    public static void comprobarDNI(String nifnie){
        String letter = "TRWAGMYFPDXBNJZSQVHLCKE";
        String dniNieRaw = nifnie;

        if(dniNieRaw.length() != 9) {
            System.out.println("DNI o NIE introducido no correcto.");
            return;
        }

        boolean isDigit = true;
        char firstChar = dniNieRaw.charAt(0);
        char dniNieLetter = dniNieRaw.charAt(dniNieRaw.length() - 1);
        String dniNie = dniNieRaw.substring(0, dniNieRaw.length() - 1);
        int dniNieNum;

        if(Character.isDigit(firstChar)){
            for (int i = 0; i < dniNie.length(); i++) {
                if (!Character.isDigit(dniNie.charAt(i))) {
                    isDigit = false;
                    break;
                }
            }
            if(isDigit){
                dniNieNum = Integer.parseInt(dniNie);
            }else{
                System.out.println("El DNI o NIE introducido tiene letras entre medias.");
                return;
            }
        }else if(firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z'){
            for (int i = 1; i < dniNie.length(); i++) {
                if (!Character.isDigit(dniNie.charAt(i))) {
                    isDigit = false;
                    break;
                }
            }

            if(isDigit){
                String correct = dniNie.replace('X', '0').replace('Y', '1').replace('Z', '2');
                dniNieNum = Integer.parseInt(correct);
            }else{
                System.out.println("El DNI o NIE introducido tiene letras entre medias.");
                return;
            }
        }else{
            System.out.println("El primer dígito no es ni X, Y o Z ni numérico.");
            return;
        }

        int rest = dniNieNum % 23;
        char check = letter.charAt(rest);

        if(dniNieLetter == check){
            System.out.println("ole que ole");
        }else{
            System.out.println("mamaste");
        }
    }
}
