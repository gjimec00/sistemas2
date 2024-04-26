/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import POJOS.Contribuyente;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import java.io.File;
import javax.xml.transform.dom.DOMSource;

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
        try{
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();

        Document document = domImplementation.createDocument(null, "ErroresNifNie", null);
        document.setXmlVersion("1.0");
        Element contribuyentesElem = document.createElement("Contribuyentes");
        
   
        contribuyentes = ExcelManager.getContribuyentesExcel("./resources/SistemasAgua.xlsx");
        for(int i = 2; i < contribuyentes.size(); i++){
            if(contribuyentes.get(i).getIdContribuyente() != 0){
            String nifnie = contribuyentes.get(i).getNifnie();
            comprobarDNI(nifnie, contribuyentes.get(i), document, contribuyentesElem);
            }else{
                System.out.println("Línea en blanco");
            }
            
        }
        
        document.getDocumentElement().appendChild(contribuyentesElem);
        Source source = new DOMSource(document);
        Result result = new StreamResult(new File("./resources/ErroresNifNie.xml"));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        }catch(Exception e){
        }
    }
    public static void comprobarDNI(String nifnie, Contribuyente contribuyente, Document document, Element contribuyentesElem){
        String letter = "TRWAGMYFPDXBNJZSQVHLCKE";
        String dniNieRaw = nifnie;

        if(dniNieRaw.length() != 9) {
            System.out.println("DNI o NIE introducido no correcto. (Longitud erronea)");
            crearXMLNifnie(contribuyente, document, contribuyentesElem);
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
                crearXMLNifnie(contribuyente, document, contribuyentesElem);
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
                crearXMLNifnie(contribuyente, document, contribuyentesElem);
                return;
            }
        }else{
            System.out.println("El primer dígito no es ni X, Y o Z ni numérico.");
            crearXMLNifnie(contribuyente, document, contribuyentesElem);
            return;
        }

        int rest = dniNieNum % 23;
        char check = letter.charAt(rest);

        if(dniNieLetter == check){
            System.out.println("DNI correcto.");
        }else{
            System.out.println("DNI incorrecto, Error subsanable.");
            crearXMLNifnie(contribuyente, document, contribuyentesElem);
        }
    }

    public static void crearXMLNifnie(Contribuyente contribuyente, Document document, Element contribuyentesElem){
        Element contribuyenteElem = document.createElement("Contribuyente");
        contribuyenteElem.setAttribute("id",Integer.toString(contribuyente.getIdContribuyente()));
        
        Element nifnie = document.createElement("NIF_NIE");
        Text nifnieT = document.createTextNode(contribuyente.getNifnie());
        nifnie.appendChild(nifnieT);
        contribuyenteElem.appendChild(nifnie);

        Element nombre = document.createElement("Nombre");
        Text nombreT = document.createTextNode(contribuyente.getNombre());
        nombre.appendChild(nombreT);
        contribuyenteElem.appendChild(nombre);

        Element apellido1 = document.createElement("PrimerApellido");
        Text apellido1T = document.createTextNode(contribuyente.getApellido1());
        apellido1.appendChild(apellido1T);
        contribuyenteElem.appendChild(apellido1);

        Element apellido2 = document.createElement("SegundoApellido");
        Text apellido2T = document.createTextNode(contribuyente.getApellido2());
        apellido2.appendChild(apellido2T);
        contribuyenteElem.appendChild(apellido2);

        contribuyentesElem.appendChild(contribuyenteElem);
    }
    /*
    public static void comprobarCCCyGenIBAN(String CCC){
        if(CCC.length() != 20){
            System.out.println("Cagaste");
            return;
        }

        int lett = 0;
        do {
            if (Character.isLetter(CCC.charAt(lett))) {
                System.out.println("Hay letras en la cadena dada");
                return;
            }
            lett++;
        } while (lett != 20);

        String nrbe = CCC.substring(0,4);
        String office = CCC.substring(4,8);
        String control = CCC.substring(8,10);
        String id = CCC.substring(10);
        int[] facts = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};

        String nrbe_office = CCC.substring(0,8);
        int[] firstDigitCheck = new int[10];
        int[] secondDigitCheck = new int[10];

        firstDigitCheck[0] = 0;
        firstDigitCheck[1] = 0;

        int CCCI = 0;
        for(int i = 2; i < firstDigitCheck.length; i++){
            firstDigitCheck[i] = Character.getNumericValue(nrbe_office.charAt(CCCI));
            CCCI++;
        }

        for(int i = 0; i < secondDigitCheck.length; i++){
            secondDigitCheck[i] = Character.getNumericValue(id.charAt(i));
        }

        int firstSum = 0;
        int secondSum = 0;

        for(int i = 0; i < firstDigitCheck.length; i++){
            firstSum += firstDigitCheck[i] * facts[i];
        }

        for(int i = 0; i < secondDigitCheck.length; i++){
            secondSum += secondDigitCheck[i] * facts[i];
        }

        int firstRest = firstSum % 11;
        int secondRest = secondSum % 11;

        int firstDigit = 11 - firstRest;
        int secondDigit = 11 - secondRest;

        if(firstDigit == 11){
            firstDigit = 0;
        }

        if(secondDigit == 11){
            secondDigit = 0;
        }

        char[] CorrectedCCC = CCC.toCharArray();


        if(Character.getNumericValue(control.charAt(0)) != firstDigit){
            System.out.println("El primer dígito de control no es correcto.");
            CorrectedCCC[8] = String.valueOf(firstDigit).charAt(0);
        }

        if(Character.getNumericValue(control.charAt(1)) != secondDigit){
            System.out.println("El segundo dígito de control no es correcto.");
            CorrectedCCC[9] = String.valueOf(secondDigit).charAt(0);
        }


        System.out.println(CCC + "\n" + nrbe + "\n" + office + "\n" + control + "\n" + id + "\n" + nrbe_office + "\n" + Arrays.toString(firstDigitCheck) + "\n" + Arrays.toString(secondDigitCheck) + "\n" + firstDigit + "\n" + secondDigit + "\n" + Arrays.toString(CorrectedCCC));
    
    }*/
}
