/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import POJOS.Contribuyente;
import POJOS.Ordenanza;
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
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;
import java.util.Scanner;
import java.text.DecimalFormat;

/**
 *
 * @author Guille & Ovi 😎
 */
public class Practicasistemas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Map < Integer, Contribuyente > contribuyentes = new LinkedHashMap < > ();
        Map < Integer, Ordenanza > ordenanzas = new LinkedHashMap < > ();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce el trimestre y año del que se desean generar recibos: ");
        //String trimestre = scanner.nextLine();
        try {
            //Errores DNI XML
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            DOMImplementation domImplementation = builder.getDOMImplementation();

            Document document = domImplementation.createDocument(null, null, null);
            document.setXmlVersion("1.0");
            Element contribuyentesElem = document.createElement("Contribuyentes");
            //Errores CCC XML
            DocumentBuilderFactory builderFactoryCcc = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderCcc = builderFactoryCcc.newDocumentBuilder();
            DOMImplementation domImplementationCcc = builderCcc.getDOMImplementation();

            Document documentCcc = domImplementationCcc.createDocument(null, null, null);
            documentCcc.setXmlVersion("1.0");
            Element cuentasElem = documentCcc.createElement("Cuentas");
            //Recibos XML
            DocumentBuilderFactory builderFactoryRecib = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderRecib = builderFactoryRecib.newDocumentBuilder();
            DOMImplementation domImplementationRecib = builderRecib.getDOMImplementation();

            Document documentRecib = domImplementationRecib.createDocument(null, null, null);
            documentCcc.setXmlVersion("1.0");
            Element recibosElem = documentRecib.createElement("Recibos");


            contribuyentes = ExcelManager.getContribuyentesExcel("./resources/SistemasAgua.xlsx");
            ordenanzas = ExcelManager.getOrdenanzasExcel("./resources/SistemasAgua.xlsx");
            Map < Integer, String > listanifnies = new LinkedHashMap < > ();
            for (int i = 2; i < contribuyentes.size(); i++) {
                if (contribuyentes.get(i).getIdContribuyente() != 0) {
                    String nifnie = contribuyentes.get(i).getNifnie();
                    comprobarDNI(nifnie, contribuyentes.get(i), document, contribuyentesElem, listanifnies);
                    comprobarCCC(contribuyentes.get(i), documentCcc, cuentasElem);
                    generarRecibo(contribuyentes.get(i), ordenanzas);
                } else {
                    System.out.println("Línea en blanco");
                }

            }
            //Errores DNI XML
            if (document.getDocumentElement() == null) {
                document.appendChild(contribuyentesElem);
            }
            Source source = new DOMSource(document);
            Result result = new StreamResult(new File("./resources/ErroresNifNie.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            //Errores CCC XML
            if (documentCcc.getDocumentElement() == null) {
                documentCcc.appendChild(cuentasElem);
            }
            Source sourceCcc = new DOMSource(documentCcc);
            Result resultCcc = new StreamResult(new File("./resources/ErroresCCC.xml"));
            Transformer transformerCcc = TransformerFactory.newInstance().newTransformer();
            transformerCcc.setOutputProperty(OutputKeys.INDENT, "yes");
            transformerCcc.transform(sourceCcc, resultCcc);
            //Recibos XML
            if (documentRecib.getDocumentElement() == null) {
                documentRecib.appendChild(recibosElem);
            }
            Source sourceRecib = new DOMSource(documentRecib);
            Result resultRecib = new StreamResult(new File("./resources/Recibos.xml"));
            Transformer transformerRecib = TransformerFactory.newInstance().newTransformer();
            transformerRecib.setOutputProperty(OutputKeys.INDENT, "yes");
            transformerRecib.transform(sourceRecib, resultRecib);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void comprobarDNI(String nifnie, Contribuyente contribuyente, Document document, Element contribuyentesElem, Map listanifnies) {
        String letter = "TRWAGMYFPDXBNJZSQVHLCKE";
        String dniNieRaw = nifnie;
        
        if (dniNieRaw.length() != 9) {
            System.out.println("DNI o NIE introducido no correcto. (Longitud erronea)");
            crearXMLNifnie(contribuyente, document, contribuyentesElem);
            return;
        }

        boolean isDigit = true;
        char firstChar = dniNieRaw.charAt(0);
        char dniNieLetter = dniNieRaw.charAt(dniNieRaw.length() - 1);
        String dniNie = dniNieRaw.substring(0, dniNieRaw.length() - 1);
        int dniNieNum;

        if (Character.isDigit(firstChar)) {
            for (int i = 0; i < dniNie.length(); i++) {
                if (!Character.isDigit(dniNie.charAt(i))) {
                    isDigit = false;
                    break;
                }
            }
            if (isDigit) {
                dniNieNum = Integer.parseInt(dniNie);
            } else {
                System.out.println("El DNI o NIE introducido tiene letras entre medias.");
                crearXMLNifnie(contribuyente, document, contribuyentesElem);
                return;
            }
        } else if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') {
            for (int i = 1; i < dniNie.length(); i++) {
                if (!Character.isDigit(dniNie.charAt(i))) {
                    isDigit = false;
                    break;
                }
            }

            if (isDigit) {
                String correct = dniNie.replace('X', '0').replace('Y', '1').replace('Z', '2');
                dniNieNum = Integer.parseInt(correct);
            } else {
                System.out.println("El DNI o NIE introducido tiene letras entre medias.");
                crearXMLNifnie(contribuyente, document, contribuyentesElem);
                return;
            }
        } else {
            System.out.println("El primer dígito no es ni X, Y o Z ni numérico.");
            crearXMLNifnie(contribuyente, document, contribuyentesElem);
            return;
        }

        int rest = dniNieNum % 23;
        char check = letter.charAt(rest);

        if (dniNieLetter == check) {
            System.out.println("DNI correcto.");
        } else {
            System.out.println("DNI incorrecto, Error subsanable.");
            String dniCorregido = dniNie + check;
            contribuyente.setNifnie(dniCorregido);
            ExcelManager.setNewContribuyente("./resources/SistemasAgua.xlsx", contribuyente);
        }
        if (!listanifnies.containsValue(contribuyente.getNifnie())) {
            listanifnies.put(contribuyente.getIdContribuyente(), contribuyente.getNifnie());
        } else {
            crearXMLNifnie(contribuyente, document, contribuyentesElem);
        }
    }

    public static void crearXMLNifnie(Contribuyente contribuyente, Document document, Element contribuyentesElem) {
        Element contribuyenteElem = document.createElement("Contribuyente");
        contribuyenteElem.setAttribute("id", Integer.toString(contribuyente.getIdContribuyente()));

        if (contribuyente.getNifnie() != null) {
            Element nifnie = document.createElement("NIF_NIE");
            Text nifnieT = document.createTextNode(contribuyente.getNifnie());
            nifnie.appendChild(nifnieT);
            contribuyenteElem.appendChild(nifnie);
        } else {
            Element nifnie = document.createElement("NIF_NIE");
            Text nifnieT = document.createTextNode("");
            nifnie.appendChild(nifnieT);
            contribuyenteElem.appendChild(nifnie);
        }

        if (contribuyente.getNombre() != null) {
            Element nombre = document.createElement("Nombre");
            Text nombreT = document.createTextNode(contribuyente.getNombre());
            nombre.appendChild(nombreT);
            contribuyenteElem.appendChild(nombre);
        } else {
            Element nombre = document.createElement("Nombre");
            Text nombreT = document.createTextNode("");
            nombre.appendChild(nombreT);
            contribuyenteElem.appendChild(nombre);
        }

        if (contribuyente.getApellido1() != null) {
            Element apellido1 = document.createElement("PrimerApellido");
            Text apellido1T = document.createTextNode(contribuyente.getApellido1());
            apellido1.appendChild(apellido1T);
            contribuyenteElem.appendChild(apellido1);
        } else {
            Element apellido1 = document.createElement("PrimerApellido");
            Text apellido1T = document.createTextNode("");
            apellido1.appendChild(apellido1T);
            contribuyenteElem.appendChild(apellido1);
        }

        if (contribuyente.getApellido2() != null) {
            Element apellido2 = document.createElement("SegundoApellido");
            Text apellido2T = document.createTextNode(contribuyente.getApellido2());
            apellido2.appendChild(apellido2T);
            contribuyenteElem.appendChild(apellido2);
        } else {
            Element apellido2 = document.createElement("SegundoApellido");
            Text apellido2T = document.createTextNode("");
            apellido2.appendChild(apellido2T);
            contribuyenteElem.appendChild(apellido2);
        }
        contribuyentesElem.appendChild(contribuyenteElem);
    }
    public static void crearXMLccc(Contribuyente contribuyente, Document documentCcc, Element cuentasElem) {
        Element cuentaElem = documentCcc.createElement("Cuenta");
        cuentaElem.setAttribute("id", Integer.toString(contribuyente.getIdContribuyente()));

        if (contribuyente.getNombre() != null) {
            Element nombre = documentCcc.createElement("Nombre");
            Text nombreT = documentCcc.createTextNode(contribuyente.getNombre());
            nombre.appendChild(nombreT);
            cuentaElem.appendChild(nombre);
        } else {
            Element nombre = documentCcc.createElement("Nombre");
            Text nombreT = documentCcc.createTextNode("");
            nombre.appendChild(nombreT);
            cuentaElem.appendChild(nombre);
        }

        Element apellidos = documentCcc.createElement("Apellidos");
        if (contribuyente.getApellido1() != null) {
            Text apellido1T = documentCcc.createTextNode(contribuyente.getApellido1() + " ");
            apellidos.appendChild(apellido1T);
        } else {
            Text apellido1T = documentCcc.createTextNode("");
            apellidos.appendChild(apellido1T);
        }
        if(contribuyente.getApellido2() != null){
            Text apellido2T = documentCcc.createTextNode(contribuyente.getApellido2());
            apellidos.appendChild(apellido2T);
        }else{
            Text apellido2T = documentCcc.createTextNode("");
            apellidos.appendChild(apellido2T);
        }
        cuentaElem.appendChild(apellidos);

        if (contribuyente.getNifnie() != null) {
            Element nifnie = documentCcc.createElement("NIFNIE");
            Text nifnieT = documentCcc.createTextNode(contribuyente.getNifnie());
            nifnie.appendChild(nifnieT);
            cuentaElem.appendChild(nifnie);
        } else {
            Element nifnie = documentCcc.createElement("NIFNIE");
            Text nifnieT = documentCcc.createTextNode("");
            nifnie.appendChild(nifnieT);
            cuentaElem.appendChild(nifnie);
        }

        if (contribuyente.getCcc() != null) {
            Element ccc = documentCcc.createElement("CCCErroneo");
            Text cccT = documentCcc.createTextNode(contribuyente.getCcc());
            ccc.appendChild(cccT);
            cuentaElem.appendChild(ccc);
        } else {
            Element ccc = documentCcc.createElement("CCCErroneo");
            Text cccT = documentCcc.createTextNode("");
            ccc.appendChild(cccT);
            cuentaElem.appendChild(ccc);
        }

        if (contribuyente.getIban() != null) {
            Element iban = documentCcc.createElement("IBANCorrecto");
            Text ibanT = documentCcc.createTextNode(contribuyente.getIban());
            iban.appendChild(ibanT);
            cuentaElem.appendChild(iban);
        } else {
            Element iban = documentCcc.createElement("IBANCorrecto");
            Text ibanT = documentCcc.createTextNode("");
            iban.appendChild(ibanT);
            cuentaElem.appendChild(iban);
        }
        cuentasElem.appendChild(cuentaElem);




    }

    
    public static void comprobarCCC(Contribuyente contribuyente, Document documentCcc, Element cuentasElem){
    //Creación alfabeto
        String abcd = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int[] abcdNum = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
        String CCC = contribuyente.getCcc();
        //Comprobación subsanabilidad
        if(CCC.length() != 20){
            System.out.println("La longitud de la cuenta no es correcta.");
            crearXMLccc(contribuyente, documentCcc, cuentasElem);
            return;
        }
        
        //Comprobación letras en CCC (en caso de haber no es subsanable)
        int lett = 0;
        do {
            if (Character.isLetter(CCC.charAt(lett))) {
                System.out.println("Hay letras en la cadena dada.");
                crearXMLccc(contribuyente, documentCcc, cuentasElem);
                return;
            }
            lett++;
        } while (lett != 20);

        //Obtención NRBE, Número de oficina, Número de control e ID.
        String nrbe_office = CCC.substring(0,8);
        String control = CCC.substring(8,10);
        String id = CCC.substring(10);
        int[] facts = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};

        int[] nrbeOfficeCheck = new int[10];
        int[] idCheck = new int[10];

        //Inicializamos el número NRBE y Office con 00 delante.
        nrbeOfficeCheck[0] = 0;
        nrbeOfficeCheck[1] = 0;

        int CCCI = 0;
        for(int i = 2; i < nrbeOfficeCheck.length; i++){
            nrbeOfficeCheck[i] = Character.getNumericValue(nrbe_office.charAt(CCCI));
            CCCI++;
        }

        for(int i = 0; i < idCheck.length; i++){
            idCheck[i] = Character.getNumericValue(id.charAt(i));
        }

        int firstSum = 0;
        int secondSum = 0;

        //Obtenemos las sumas totales de los dos números en conjunto para posteriormente obtener los dígitos.
        for(int i = 0; i < nrbeOfficeCheck.length; i++){
            firstSum += nrbeOfficeCheck[i] * facts[i];
        }

        for(int i = 0; i < idCheck.length; i++){
            secondSum += idCheck[i] * facts[i];
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

        //Convertimos el String a un array de chars para operar con ello y obtener el número final
        char[] CorrectedCCC = CCC.toCharArray();
        String CorrectedCCCStd;
        boolean check = false;

        //Comprobamos si coincide el número de control válido con el existente, en caso de no coincidir subsana.
        char firstDigitStr = String.valueOf(firstDigit).charAt(0);
        if(Character.getNumericValue(control.charAt(0)) != Character.getNumericValue(firstDigitStr)){
            System.out.println("El primer dígito de control no es correcto.");
            CorrectedCCC[8] = String.valueOf(firstDigit).charAt(0);
            check = true;

        }
        char secondDigitStr = String.valueOf(secondDigit).charAt(0);
        if(Character.getNumericValue(control.charAt(1)) != Character.getNumericValue(secondDigitStr)){
            System.out.println("El segundo dígito de control no es correcto.");
            CorrectedCCC[9] = String.valueOf(secondDigit).charAt(0);
            check = true;
        }

        
        CorrectedCCCStd = new String(CorrectedCCC);

        //Obtenemos un array de chars a partir del CCC e implementando los números de las letras del país
        char[] IBAN = new char[26];
        System.arraycopy(CorrectedCCC, 0, IBAN, 0, CorrectedCCC.length);

        char firstIbanLetter = contribuyente.getPaisCcc().charAt(0); //Crear String PaisCCC y PaisCCC.charAt(0)
        char secondIbanLetter = contribuyente.getPaisCcc().charAt(1); //Crear String PaisCCC y PaisCCC.charAt(1)

        for (int i = 0; i < abcd.length(); i++) {
            if(firstIbanLetter == abcd.charAt(i)){
                int num = abcdNum[i];
                for (int j = 0; j < 2; j++) {
                    IBAN[j + 20] = Integer.toString(num).charAt(j);
                }
            }
        }

        for (int i = 0; i < abcd.length(); i++) {
            if(secondIbanLetter == abcd.charAt(i)){
                int num = abcdNum[i];
                for (int j = 0; j < 2; j++) {
                    IBAN[j + 22] = Integer.toString(num).charAt(j);
                }
            }
        }

        IBAN[24] = '0';
        IBAN[25] = '0';

        //Creamos un String a partir del array de chars con números del IBAN para operar en él aplicando el módulo
        String IBANStr = new String(IBAN);
        BigInteger IBANNum = new BigInteger(IBANStr);
        BigInteger IBANModule = IBANNum.mod(BigInteger.valueOf(97));

        int diff = 98 - IBANModule.intValue();
        char[] conv = new char[2];

        for (int i = 0; i < Integer.toString(diff).length(); i++) {
            conv[i] = Integer.toString(diff).charAt(i);
        }
        if(conv[1] == '\u0000'){
            conv[1] = conv[0];
            conv[0] = '0';
        }

        //Comprobamos si los dígitos de control obtenidos son válidos.
        StringBuilder correctedIBAN = new StringBuilder();
        for(int i = 0; i < 24; i++){
            if(i == 0){
                correctedIBAN.append(contribuyente.getPaisCcc().charAt(i));
            }else if(i == 1){
                correctedIBAN.append(contribuyente.getPaisCcc().charAt(i));
            }else if(i == 2) {
                correctedIBAN.append(conv[0]);
            }else if(i == 3){
                correctedIBAN.append(conv[1]);
            }else{
                correctedIBAN.append(CorrectedCCC[i - 4]);
            }
        }
        contribuyente.setIban(correctedIBAN.toString());
        if(check){crearXMLccc(contribuyente, documentCcc, cuentasElem);}
        contribuyente.setCcc(CorrectedCCCStd);
        ExcelManager.setNewCCCIBAN("./resources/SistemasAgua.xlsx", contribuyente);
        
        //System.out.println(Arrays.toString(nrbeOfficeCheck) + "\n" + Arrays.toString(idCheck) + "\n" + firstDigit + "\n" + secondDigit + "\n" + Arrays.toString(CorrectedCCC) + "\n" + correctedIBAN);
    
    }
    public static void generarRecibo(Contribuyente contribuyente, Map < Integer, Ordenanza > ordenanzas){
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println(contribuyente.getNombre());
        List<String> lecturas = new ArrayList<>(contribuyente.getLecturases());
        if(lecturas.size() == 1){
            lecturas.add("0");
        }
        double diferenciaLecturas = Math.abs(Double.parseDouble(lecturas.get(1)) - Double.parseDouble(lecturas.get(0)));

        List<String> conceptosCobrar = new ArrayList<>(contribuyente.getRelContribuyenteOrdenanzas());

        Map<String, Double> listaConceptos = new LinkedHashMap<>();
        double costeTotal = 0;
        List<Integer> diferidos = new ArrayList<>();
        double ivaTotal = 0;

        for(int i = 0; i < conceptosCobrar.size(); i++){
            int metrosAcumulados = 0;
            double costeConcepto = 0;
            double costeConceptoSinD = 0;

            int conceptoId = Integer.parseInt(conceptosCobrar.get(i));
            for(int j = 2; j < ordenanzas.size() + 2; j++){
                double costeTramo = 0;
                //System.out.println("Concepto: " + Integer.parseInt(conceptosCobrar.get(i)) + " Ordenanza" + ordenanzas.get(j).getIdOrdenanza());
                if(Integer.parseInt(conceptosCobrar.get(i)) == ordenanzas.get(j).getIdOrdenanza()){
                    //System.out.println(ordenanzas.get(j).getAcumulable() + "\n" + ordenanzas.get(j).getPrecioFijo() + "\n" + conceptosCobrar.get(i).toString());
                    if(ordenanzas.get(j).getSubconcepto().equals("Fijo") && ordenanzas.get(j).getM3incluidos() != null){
                        costeTramo += ordenanzas.get(j).getPrecioFijo();
                        if(ordenanzas.get(j).getAcumulable().equals("S")){
                            metrosAcumulados += ordenanzas.get(j).getM3incluidos();
                            if(metrosAcumulados > diferenciaLecturas){
                                diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                            }
                        }else{
                            diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                        }
                        
                        if(diferenciaLecturas < 0){
                            diferenciaLecturas = 0;
                        }
                    }
                    
                    if(ordenanzas.get(j).getSubconcepto().equals("Fijo") && ordenanzas.get(j).getM3incluidos() == null && ordenanzas.get(j).getPorcentaje() == null){
                        costeTramo += ordenanzas.get(j).getPrecioFijo();
                    }
                    //System.out.println("Concepto: " + ordenanzas.get(j).getSubconcepto() + " " + ordenanzas.get(j).getM3incluidos() + " " + ordenanzas.get(j).getPorcentaje());
                    if(ordenanzas.get(j).getSubconcepto().equals("Fijo") && ordenanzas.get(j).getPrecioFijo() == null && ordenanzas.get(j).getPorcentaje() != null){
                        //Si el conceptoCobrar es mayor que el idOrdenanza que tenemos, añadir al final de la lista la ordenanza
                        //para que la cobre cuando ya haya calculado el concepto siguiente
                        if(Integer.parseInt(conceptosCobrar.get(i)) < ordenanzas.get(j).getConceptoRelacionado()){
                            diferidos.add(conceptoId);
                            break;
                        }else{
                            double baseOtroConcepto = listaConceptos.get(ordenanzas.get(j).getConceptoRelacionado().toString());
                            baseOtroConcepto = baseOtroConcepto * ordenanzas.get(j).getPorcentaje() / 100;
                            costeTramo += baseOtroConcepto;
                            
                        }
                        
                    }
                    
                    if(ordenanzas.get(j).getSubconcepto().contains("Desagüe")){
                        if(Integer.parseInt(conceptosCobrar.get(i)) < ordenanzas.get(j).getConceptoRelacionado()){
                            diferidos.add(conceptoId);
                            break;
                        }else{
                            double baseOtroConcepto = listaConceptos.get(ordenanzas.get(j).getConceptoRelacionado().toString());
                            baseOtroConcepto = baseOtroConcepto * ordenanzas.get(j).getPorcentaje() / 100;
                            costeTramo += baseOtroConcepto;
                        }
                    }

                    if(ordenanzas.get(j).getSubconcepto().contains("tramo") && ordenanzas.get(j).getAcumulable().equals("N")){
                        int metrosUsados = 0;
                        while(diferenciaLecturas > 0 && metrosUsados != ordenanzas.get(j).getM3incluidos()){
                            costeTramo = costeTramo + ordenanzas.get(j).getPreciom3() * 1;
                            metrosUsados++;
                            diferenciaLecturas--;
                        }
                    }
                    
                    if(ordenanzas.get(j).getSubconcepto().contains("tramo") && ordenanzas.get(j).getAcumulable().equals("S")){
                        int metrosUsados = 0;     
                        metrosAcumulados += ordenanzas.get(j).getM3incluidos();
                        if(diferenciaLecturas < metrosAcumulados){
                            while(diferenciaLecturas > 0 && metrosUsados != metrosAcumulados){
                                costeTramo += ordenanzas.get(j).getPreciom3() * 1;
                                metrosUsados++;
                                diferenciaLecturas--;
                            }
                        }
                    }
                    if(ordenanzas.get(j).getM3incluidos() != null && ordenanzas.get(j).getPrecioFijo() != null && ordenanzas.get(j).getPreciom3() != null){
                        costeTramo += ordenanzas.get(j).getPrecioFijo();
                        if(diferenciaLecturas > ordenanzas.get(j).getM3incluidos()){
                            while(diferenciaLecturas > 0){
                                costeTramo += ordenanzas.get(j).getPreciom3() * 1;
                                diferenciaLecturas--;
                            }
                        }else{
                            diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                            if(diferenciaLecturas < 0){
                                diferenciaLecturas = 0;
                            }
                        } 
                    }
                    costeConceptoSinD += costeTramo;
                    if(contribuyente.getBonificacion() != 0){
                        double bonificacion = costeTramo * contribuyente.getBonificacion() / 100;
                        costeTramo = costeTramo - bonificacion;
                    }
                    double ivaTramo = costeTramo * ordenanzas.get(j).getIva() / 100;
                    ivaTotal += ivaTramo;
                    costeConcepto += costeTramo;
                    System.out.println(df.format(costeTramo) + " iva tramo: " + ivaTramo);
                }
            }
            if(!diferidos.contains(conceptoId)){
                costeTotal += costeConcepto;
                listaConceptos.put(conceptosCobrar.get(i), costeConceptoSinD);
                //System.out.println(df.format(costeConcepto));
            }
        }

        for(int diferido : diferidos){
            double costeConcepto = 0;
            double costeTramo = 0;
            double ivaTramo = 0;
            double costeConceptoSinD = 0;
            //System.out.println(diferido);
            for(int j = 2; j < ordenanzas.size() + 2; j++){
                if (diferido == ordenanzas.get(j).getIdOrdenanza()){
                    double baseOtroConcepto = listaConceptos.get(ordenanzas.get(j).getConceptoRelacionado().toString());
                    baseOtroConcepto = baseOtroConcepto * ordenanzas.get(j).getPorcentaje() / 100;
                    costeTramo += baseOtroConcepto;
                    costeConceptoSinD += costeTramo;
                    if(contribuyente.getBonificacion() != 0){
                    double bonificacion = costeTramo * contribuyente.getBonificacion() / 100;
                    costeTramo = costeTramo - bonificacion;
                    }
                    ivaTramo = costeTramo * ordenanzas.get(j).getIva() / 100;
                    costeConcepto += costeTramo;
                }
                
            }
                ivaTotal += ivaTramo;
                System.out.println(df.format(costeConcepto) + " iva tramo: " + ivaTramo);
                costeTotal += costeTramo;
                listaConceptos.put(String.valueOf(diferido), costeConceptoSinD);
            
        }
        System.out.println(contribuyente.getNombre() + " sin iva: " + costeTotal + " iva: " + ivaTotal);
        costeTotal += ivaTotal;
        System.out.println(contribuyente.getNombre() + ": " + costeTotal);
    
    }
}