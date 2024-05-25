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
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.io.IOException;
import POJOS.HibernateUtil;
import POJOS.Lineasrecibo;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import POJOS.Recibos;
import POJOS.Lecturas;
import POJOS.RelContribuyenteOrdenanza;
/**
 *
 * @author Guille & Ovi 😎
 */
public class Practicasistemas {

    /**
     * @param args the command line arguments
     */
    public static int contador = 0;
    public static double totalBaseImponible = 0;
    public static double totalIva = 0;
    public static DecimalFormat df = new DecimalFormat("0.00");
    public static String input;
    public static void main(String[] args) throws IOException {
        Map < Integer, Contribuyente > contribuyentes = new LinkedHashMap < > ();
        Map < Integer, Ordenanza > ordenanzas = new LinkedHashMap < > ();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce el trimestre y año del que se desean generar recibos: ");
        input = scanner.nextLine();
        String trimestre = input.substring(0, 2);
        int year = Integer.parseInt(input.substring(3));
        Date fechaFinTr;
        if (trimestre.equals("1T")) {
            fechaFinTr = getLastDayOfTrimestre(year, Calendar.MARCH);
        } else if (trimestre.equals("2T")) {
            fechaFinTr = getLastDayOfTrimestre(year, Calendar.JUNE);
        } else if (trimestre.equals("3T")) {
            fechaFinTr = getLastDayOfTrimestre(year, Calendar.SEPTEMBER);
        } else {
            fechaFinTr = getLastDayOfTrimestre(year, Calendar.DECEMBER);
        }
        System.out.println(fechaFinTr);
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
            recibosElem.setAttribute("fechaPadron", trimestre + " de " + year);


            contribuyentes = ExcelManager.getContribuyentesExcel("./resources/SistemasAgua.xlsx");
            ordenanzas = ExcelManager.getOrdenanzasExcel("./resources/SistemasAgua.xlsx");
            for (int i = 2; i < ordenanzas.size() + 2; i++){
                DBManager.saveOrdenanzas(ordenanzas.get(i));
            }
            Map < Integer, String > listanifnies = new LinkedHashMap < > ();
            for (int i = 2; i < contribuyentes.size(); i++) {
                if (contribuyentes.get(i).getIdContribuyente() != 0) {
                    String nifnie = contribuyentes.get(i).getNifnie();
                    comprobarCCC(contribuyentes.get(i), documentCcc, cuentasElem);
                    comprobarDNI(nifnie, contribuyentes.get(i), document, contribuyentesElem, listanifnies, ordenanzas, fechaFinTr, documentRecib, recibosElem);

                } else {
                    System.out.println("Línea en blanco");
                }

            }
            GeneratePDF.generarResumen(totalBaseImponible, totalIva, input);
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
            recibosElem.setAttribute("totalBaseImponible", df.format(totalBaseImponible));
            recibosElem.setAttribute("totalIva", df.format(totalIva));
            double totalRecibos = totalBaseImponible + totalIva;
            recibosElem.setAttribute("totalRecibos", df.format(totalRecibos));
            if (documentRecib.getDocumentElement() == null) {
                documentRecib.appendChild(recibosElem);
            }
            Source sourceRecib = new DOMSource(documentRecib);
            Result resultRecib = new StreamResult(new File("./resources/Recibos.xml"));
            Transformer transformerRecib = TransformerFactory.newInstance().newTransformer();
            transformerRecib.setOutputProperty(OutputKeys.INDENT, "yes");
            transformerRecib.transform(sourceRecib, resultRecib);

            HibernateUtil.shutdown();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void comprobarDNI(String nifnie, Contribuyente contribuyente, Document document, Element contribuyentesElem, Map listanifnies, Map < Integer, Ordenanza > ordenanzas, Date fechaFinTr, Document documentRecib, Element recibosElem) throws IOException{
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
            generarRecibo(contribuyente, ordenanzas, fechaFinTr, documentRecib, recibosElem);
            DBManager.saveContribuyente(contribuyente);
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
        if (contribuyente.getApellido2() != null) {
            Text apellido2T = documentCcc.createTextNode(contribuyente.getApellido2());
            apellidos.appendChild(apellido2T);
        } else {
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


    public static void comprobarCCC(Contribuyente contribuyente, Document documentCcc, Element cuentasElem) {
        //Creación alfabeto
        String abcd = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int[] abcdNum = {
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28,
            29,
            30,
            31,
            32,
            33,
            34,
            35
        };
        String CCC = contribuyente.getCcc();
        //Comprobación subsanabilidad
        if (CCC.length() != 20) {
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
        String nrbe_office = CCC.substring(0, 8);
        String control = CCC.substring(8, 10);
        String id = CCC.substring(10);
        int[] facts = {
            1,
            2,
            4,
            8,
            5,
            10,
            9,
            7,
            3,
            6
        };

        int[] nrbeOfficeCheck = new int[10];
        int[] idCheck = new int[10];

        //Inicializamos el número NRBE y Office con 00 delante.
        nrbeOfficeCheck[0] = 0;
        nrbeOfficeCheck[1] = 0;

        int CCCI = 0;
        for (int i = 2; i < nrbeOfficeCheck.length; i++) {
            nrbeOfficeCheck[i] = Character.getNumericValue(nrbe_office.charAt(CCCI));
            CCCI++;
        }

        for (int i = 0; i < idCheck.length; i++) {
            idCheck[i] = Character.getNumericValue(id.charAt(i));
        }

        int firstSum = 0;
        int secondSum = 0;

        //Obtenemos las sumas totales de los dos números en conjunto para posteriormente obtener los dígitos.
        for (int i = 0; i < nrbeOfficeCheck.length; i++) {
            firstSum += nrbeOfficeCheck[i] * facts[i];
        }

        for (int i = 0; i < idCheck.length; i++) {
            secondSum += idCheck[i] * facts[i];
        }

        int firstRest = firstSum % 11;
        int secondRest = secondSum % 11;

        int firstDigit = 11 - firstRest;
        int secondDigit = 11 - secondRest;

        if (firstDigit == 11) {
            firstDigit = 0;
        }

        if (secondDigit == 11) {
            secondDigit = 0;
        }

        //Convertimos el String a un array de chars para operar con ello y obtener el número final
        char[] CorrectedCCC = CCC.toCharArray();
        String CorrectedCCCStd;
        boolean check = false;

        //Comprobamos si coincide el número de control válido con el existente, en caso de no coincidir subsana.
        char firstDigitStr = String.valueOf(firstDigit).charAt(0);
        if (Character.getNumericValue(control.charAt(0)) != Character.getNumericValue(firstDigitStr)) {
            System.out.println("El primer dígito de control no es correcto.");
            CorrectedCCC[8] = String.valueOf(firstDigit).charAt(0);
            check = true;

        }
        char secondDigitStr = String.valueOf(secondDigit).charAt(0);
        if (Character.getNumericValue(control.charAt(1)) != Character.getNumericValue(secondDigitStr)) {
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
            if (firstIbanLetter == abcd.charAt(i)) {
                int num = abcdNum[i];
                for (int j = 0; j < 2; j++) {
                    IBAN[j + 20] = Integer.toString(num).charAt(j);
                }
            }
        }

        for (int i = 0; i < abcd.length(); i++) {
            if (secondIbanLetter == abcd.charAt(i)) {
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
        if (conv[1] == '\u0000') {
            conv[1] = conv[0];
            conv[0] = '0';
        }

        //Comprobamos si los dígitos de control obtenidos son válidos.
        StringBuilder correctedIBAN = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            if (i == 0) {
                correctedIBAN.append(contribuyente.getPaisCcc().charAt(i));
            } else if (i == 1) {
                correctedIBAN.append(contribuyente.getPaisCcc().charAt(i));
            } else if (i == 2) {
                correctedIBAN.append(conv[0]);
            } else if (i == 3) {
                correctedIBAN.append(conv[1]);
            } else {
                correctedIBAN.append(CorrectedCCC[i - 4]);
            }
        }
        contribuyente.setIban(correctedIBAN.toString());
        if (check) {
            crearXMLccc(contribuyente, documentCcc, cuentasElem);
        }
        contribuyente.setCcc(CorrectedCCCStd);
        ExcelManager.setNewCCCIBAN("./resources/SistemasAgua.xlsx", contribuyente);

        //System.out.println(Arrays.toString(nrbeOfficeCheck) + "\n" + Arrays.toString(idCheck) + "\n" + firstDigit + "\n" + secondDigit + "\n" + Arrays.toString(CorrectedCCC) + "\n" + correctedIBAN);

    }
    public static void generarRecibo(Contribuyente contribuyente, Map < Integer, Ordenanza > ordenanzas, Date fechaFinTr, Document documentRecib, Element recibosElem) throws IOException{
        if (fechaFinTr.after(contribuyente.getFechaAlta()) && (contribuyente.getFechaBaja() == null || contribuyente.getFechaBaja().after(fechaFinTr))) {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            String lastRecHQL = "SELECT COALESCE(MAX(r.numeroRecibo), 0) FROM Recibos r";
            Query lastRecQuery = session.createQuery(lastRecHQL);
            
            int lastIdRecibo = (int) lastRecQuery.uniqueResult() + 1;
            tx.commit();
            Recibos recibo = new Recibos();
            RelContribuyenteOrdenanza relConOrd = new RelContribuyenteOrdenanza();
            List < Lineasrecibo >  listaLineas = new ArrayList <> ();
            System.out.println(contribuyente.getNombre());
            ArrayList<ArrayList<String>> datosRecibo = new ArrayList<>();
            List < String > lecturas = new ArrayList < > (contribuyente.getLecturases());
            String tipoCalculo = "";
            if (lecturas.size() == 1) {
                lecturas.add(lecturas.get(0));
            }
            if (Double.parseDouble(lecturas.get(1)) < Double.parseDouble(lecturas.get(0))) {
                String aux = lecturas.get(0);
                lecturas.set(0, lecturas.get(1));
                lecturas.set(1, aux);
            }
            double diferenciaLecturas = Math.abs(Double.parseDouble(lecturas.get(1)) - Double.parseDouble(lecturas.get(0)));

            List < String > conceptosCobrar = new ArrayList < > (contribuyente.getRelContribuyenteOrdenanzas());

            Map < String, Double > listaConceptos = new LinkedHashMap < > ();
            double costeTotal = 0;
            List < Integer > diferidos = new ArrayList < > ();
            double ivaTotal = 0;

            for (int i = 0; i < conceptosCobrar.size(); i++) {
                int metrosAcumulados = 0;
                double costeConcepto = 0;
                double costeConceptoSinD = 0;

                int conceptoId = Integer.parseInt(conceptosCobrar.get(i));
                for (int j = 2; j < ordenanzas.size() + 2; j++) {
                    double costeTramo = 0;
                    //System.out.println("Concepto: " + Integer.parseInt(conceptosCobrar.get(i)) + " Ordenanza" + ordenanzas.get(j).getIdOrdenanza());
                    if (Integer.parseInt(conceptosCobrar.get(i)) == ordenanzas.get(j).getIdOrdenanza()) {
                        /*StringBuilder sb = new StringBuilder();
                        sb.append("fila").append()*/
                        relConOrd.setContribuyente(contribuyente);
                        relConOrd.setOrdenanza(ordenanzas.get(j));
                        DBManager.saveRelCon(relConOrd);
                        
                        Lineasrecibo linea = new Lineasrecibo();
                        ArrayList<String> fila = new ArrayList<>();
                        fila.add(ordenanzas.get(j).getConcepto());
                        linea.setConcepto(ordenanzas.get(j).getConcepto());
                        fila.add(ordenanzas.get(j).getSubconcepto());
                        linea.setSubconcepto(ordenanzas.get(j).getSubconcepto());
                        //fila.add(df.format(diferenciaLecturas));
                        tipoCalculo = ordenanzas.get(j).getTipoCalculo();
                        
                        //System.out.println(ordenanzas.get(j).getAcumulable() + "\n" + ordenanzas.get(j).getPrecioFijo() + "\n" + conceptosCobrar.get(i).toString());
                        if (ordenanzas.get(j).getSubconcepto().equals("Fijo") && ordenanzas.get(j).getM3incluidos() != null) {
                            costeTramo += ordenanzas.get(j).getPrecioFijo();
                            if (ordenanzas.get(j).getAcumulable() != null) {
                                if (ordenanzas.get(j).getAcumulable().equals("S")) {
                                    metrosAcumulados += ordenanzas.get(j).getM3incluidos();
                                    if (metrosAcumulados > diferenciaLecturas) { 
                                        fila.add(df.format(diferenciaLecturas));
                                        linea.setM3incluidos(diferenciaLecturas);
                                        diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                                    }else{
                                        fila.add(df.format(0));
                                        linea.setM3incluidos(0);
                                    }
                                } else {
                                    if(diferenciaLecturas < ordenanzas.get(j).getM3incluidos()){
                                        fila.add(df.format(diferenciaLecturas));
                                        linea.setM3incluidos(diferenciaLecturas);
                                        diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                                    }else{
                                        fila.add(df.format(ordenanzas.get(j).getM3incluidos()));
                                        linea.setM3incluidos(ordenanzas.get(j).getM3incluidos());
                                        diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                                    }
                                    
                                    
                                    
                                }
                            }
                            if (diferenciaLecturas < 0) {
                                diferenciaLecturas = 0;
                            }
                        }

                        if (ordenanzas.get(j).getSubconcepto().equals("Fijo") && ordenanzas.get(j).getM3incluidos() == null && ordenanzas.get(j).getPorcentaje() == null) {
                            costeTramo += ordenanzas.get(j).getPrecioFijo();
                            fila.add(df.format(0));
                            linea.setM3incluidos(0);
                        }
                        //System.out.println("Concepto: " + ordenanzas.get(j).getSubconcepto() + " " + ordenanzas.get(j).getM3incluidos() + " " + ordenanzas.get(j).getPorcentaje());
                        if (ordenanzas.get(j).getSubconcepto().equals("Fijo") && ordenanzas.get(j).getPrecioFijo() == null && ordenanzas.get(j).getPorcentaje() != null) {
                            //Si el conceptoCobrar es mayor que el idOrdenanza que tenemos, añadir al final de la lista la ordenanza
                            //para que la cobre cuando ya haya calculado el concepto siguiente
                            if (Integer.parseInt(conceptosCobrar.get(i)) < ordenanzas.get(j).getConceptoRelacionado()) {
                                diferidos.add(conceptoId);
                                break;
                            } else {
                                double baseOtroConcepto = listaConceptos.get(ordenanzas.get(j).getConceptoRelacionado().toString());
                                baseOtroConcepto = baseOtroConcepto * ordenanzas.get(j).getPorcentaje() / 100;
                                costeTramo += baseOtroConcepto;
                                fila.add(df.format(0));
                                linea.setM3incluidos(0);

                            }

                        }

                        if (ordenanzas.get(j).getSubconcepto().contains("Desagüe")) {
                            if (Integer.parseInt(conceptosCobrar.get(i)) < ordenanzas.get(j).getConceptoRelacionado()) {
                                diferidos.add(conceptoId);
                                break;
                            } else {
                                double baseOtroConcepto = listaConceptos.get(ordenanzas.get(j).getConceptoRelacionado().toString());
                                baseOtroConcepto = baseOtroConcepto * ordenanzas.get(j).getPorcentaje() / 100;
                                costeTramo += baseOtroConcepto;
                                fila.add(df.format(0));
                                linea.setM3incluidos(0);
                            }
                        }

                        if (ordenanzas.get(j).getSubconcepto().contains("tramo") && ordenanzas.get(j).getAcumulable().equals("N")) {
                            int metrosUsados = 0;
                            while (diferenciaLecturas > 0 && metrosUsados != ordenanzas.get(j).getM3incluidos()) {
                                costeTramo = costeTramo + ordenanzas.get(j).getPreciom3() * 1;
                                metrosUsados++;
                                diferenciaLecturas--;
                                
                            }
                            fila.add(df.format(metrosUsados));
                            linea.setM3incluidos(metrosUsados);
                        }

                        if (ordenanzas.get(j).getSubconcepto().contains("tramo") && ordenanzas.get(j).getAcumulable().equals("S")) {
                            int metrosUsados = 0;
                            metrosAcumulados += ordenanzas.get(j).getM3incluidos();
                            if (diferenciaLecturas < metrosAcumulados) {
                                while (diferenciaLecturas > 0 && metrosUsados != metrosAcumulados) {
                                    costeTramo += ordenanzas.get(j).getPreciom3() * 1;
                                    metrosUsados++;
                                    diferenciaLecturas--;
                                    
                                }
                                fila.add(df.format(metrosUsados));
                                linea.setM3incluidos(metrosUsados);
                            }else{
                                fila.add(df.format(metrosUsados));
                                linea.setM3incluidos(metrosUsados);
                            }
                        }
                        if (ordenanzas.get(j).getM3incluidos() != null && ordenanzas.get(j).getPrecioFijo() != null && ordenanzas.get(j).getPreciom3() != null) {
                            costeTramo += ordenanzas.get(j).getPrecioFijo();
                            if (diferenciaLecturas > ordenanzas.get(j).getM3incluidos()) {
                                fila.add(df.format(diferenciaLecturas));
                                linea.setM3incluidos(diferenciaLecturas);
                                while (diferenciaLecturas > 0) {
                                    costeTramo += ordenanzas.get(j).getPreciom3() * 1;
                                    diferenciaLecturas--;
                                }
                            } else {
                                fila.add(df.format(diferenciaLecturas));
                                linea.setM3incluidos(diferenciaLecturas);
                                diferenciaLecturas = diferenciaLecturas - ordenanzas.get(j).getM3incluidos();
                                if (diferenciaLecturas < 0) {
                                    diferenciaLecturas = 0;
                                }
                            }
                        }
                        costeConceptoSinD += costeTramo;
                        linea.setBaseImponible(costeTramo);
                        if (contribuyente.getBonificacion() != 0) {
                            double bonificacion = costeTramo * contribuyente.getBonificacion() / 100;  
                            costeTramo = costeTramo - bonificacion;
                        }
                        linea.setBonificacion(contribuyente.getBonificacion());
                        linea.setImporteBonificacion(costeTramo);
                        fila.add(df.format(costeTramo));
                        fila.add(df.format(ordenanzas.get(j).getIva()));
                        linea.setPorcentajeIva(ordenanzas.get(j).getIva());
                        double ivaTramo = costeTramo * ordenanzas.get(j).getIva() / 100;
                        linea.setImporteiva(ivaTramo);
                        fila.add(df.format(ivaTramo));
                        if (contribuyente.getBonificacion() != 0) {
                            fila.add(df.format(contribuyente.getBonificacion()));
                        }
                        ivaTotal += ivaTramo;
                        costeConcepto += costeTramo;
                        System.out.println(df.format(costeTramo) + " iva tramo: " + ivaTramo);
                        datosRecibo.add(fila);
                        linea.setIdRecibo(lastIdRecibo);
                        listaLineas.add(linea);
                    }

                }
                if (!diferidos.contains(conceptoId)) {
                    costeTotal += costeConcepto;
                    listaConceptos.put(conceptosCobrar.get(i), costeConceptoSinD);
                    //System.out.println(df.format(costeConcepto));
                }
            }

            for (int diferido: diferidos) {
                double costeConcepto = 0;
                double costeTramo = 0;
                double ivaTramo = 0;
                double costeConceptoSinD = 0;
                //System.out.println(diferido);
                for (int j = 2; j < ordenanzas.size() + 2; j++) {
                    if (diferido == ordenanzas.get(j).getIdOrdenanza()) {
                        Lineasrecibo linea = new Lineasrecibo();
                        ArrayList<String> fila = new ArrayList<>();
                        fila.add(ordenanzas.get(j).getConcepto());
                        linea.setConcepto(ordenanzas.get(j).getConcepto());
                        fila.add(ordenanzas.get(j).getSubconcepto());
                        linea.setSubconcepto(ordenanzas.get(j).getSubconcepto());
                        fila.add(df.format(diferenciaLecturas));
                        linea.setM3incluidos(diferenciaLecturas);
                        double baseOtroConcepto = listaConceptos.get(ordenanzas.get(j).getConceptoRelacionado().toString());
                        baseOtroConcepto = baseOtroConcepto * ordenanzas.get(j).getPorcentaje() / 100;
                        costeTramo += baseOtroConcepto;
                        costeConceptoSinD += costeTramo;
                        linea.setBaseImponible(costeTramo);
                        if (contribuyente.getBonificacion() != 0) {
                            double bonificacion = costeTramo * contribuyente.getBonificacion() / 100;
                            costeTramo = costeTramo - bonificacion;
                        }
                        linea.setBonificacion(contribuyente.getBonificacion());
                        fila.add(df.format(costeTramo));
                        linea.setImporteBonificacion(costeTramo);
                        fila.add(df.format(ordenanzas.get(j).getIva()));
                        linea.setPorcentajeIva(ordenanzas.get(j).getIva());
                        ivaTramo = costeTramo * ordenanzas.get(j).getIva() / 100;
                        fila.add(df.format(ivaTramo));
                        linea.setImporteiva(ivaTramo);
                        if (contribuyente.getBonificacion() != 0) {
                            fila.add(df.format(contribuyente.getBonificacion()));
                        }
                        costeConcepto += costeTramo;
                        datosRecibo.add(fila);
                        linea.setIdRecibo(lastIdRecibo);
                        listaLineas.add(linea);
                    }

                }
                ivaTotal += ivaTramo;
                System.out.println(df.format(costeConcepto) + " iva tramo: " + ivaTramo);
                costeTotal += costeTramo;
                listaConceptos.put(String.valueOf(diferido), costeConceptoSinD);

            }

            System.out.println(contribuyente.getNombre() + " sin iva: " + costeTotal + " iva: " + ivaTotal);
            if (contribuyente.getExencion().equals('S')) {
                costeTotal = 0;
                ivaTotal = 0;
            }
            contribuyente.setBaseImponible(costeTotal);
            totalBaseImponible += costeTotal;
            contribuyente.setIvaRecibo(ivaTotal);
            totalIva += ivaTotal;
            recibo.setTotalBaseImponible(costeTotal);
            recibo.setTotalIva(ivaTotal);
            costeTotal += ivaTotal;
            System.out.println(contribuyente.getNombre() + ": " + costeTotal);
            crearXMLRecibos(contribuyente, documentRecib, recibosElem, lastIdRecibo);
            GeneratePDF.generarPDFs(contribuyente, datosRecibo, costeTotal, ivaTotal, tipoCalculo, input);


            recibo.setNumeroRecibo(lastIdRecibo);
            recibo.setContribuyente(contribuyente);
            recibo.setNifContribuyente(contribuyente.getNifnie());
            recibo.setDireccionCompleta(contribuyente.getDireccion() + contribuyente.getNumero());
            recibo.setNombre(contribuyente.getNombre());
            recibo.setApellidos(contribuyente.getApellido1() + contribuyente.getApellido2());
            Date today = Calendar.getInstance().getTime();
            recibo.setFechaRecibo(today);
            recibo.setLecturaAnterior((int)Double.parseDouble(lecturas.get(0)));
            recibo.setLecturaActual((int)Double.parseDouble(lecturas.get(1)));
            int consumoInt = (int)Double.parseDouble(lecturas.get(1)) - (int)Double.parseDouble(lecturas.get(0));
            recibo.setConsumom3(consumoInt);
            recibo.setFechaPadron(fechaFinTr);
            recibo.setTotalRecibo(costeTotal);
            recibo.setIban(contribuyente.getIban());
            recibo.setExencion(contribuyente.getExencion().toString());
            DBManager.saveRecibos(recibo);
            
            for (int i = 0; i < listaLineas.size(); i++){
                listaLineas.get(i).setRecibos(recibo);
                DBManager.saveLineasRecibo(listaLineas.get(i));
            }
            String trimestre = input.substring(0, 2);
            int year = Integer.parseInt(input.substring(3));
            Lecturas lectura = new Lecturas();
            lectura.setContribuyente(contribuyente);
            lectura.setEjercicio(Integer.toString(year));
            lectura.setPeriodo(trimestre);
            lectura.setLecturaAnterior((int)Double.parseDouble(lecturas.get(0)));
            lectura.setLecturaActual((int)Double.parseDouble(lecturas.get(1)));
            DBManager.saveLecturas(lectura);
            
        }
    }
    
    public static void crearXMLRecibos(Contribuyente contribuyente, Document documentRecib, Element recibosElem, int lastIdRecibo) {
        contador++;
        Element reciboElem = documentRecib.createElement("Recibo");
        reciboElem.setAttribute("idRecibo", Integer.toString(lastIdRecibo));

        if (contribuyente.getExencion() != null) {
            Element exencion = documentRecib.createElement("Exencion");
            Text exencionT = documentRecib.createTextNode(contribuyente.getExencion().toString());
            exencion.appendChild(exencionT);
            reciboElem.appendChild(exencion);
        } else {
            Element exencion = documentRecib.createElement("Exencion");
            Text exencionT = documentRecib.createTextNode("");
            exencion.appendChild(exencionT);
            reciboElem.appendChild(exencion);
        }

        if (contribuyente.getIdContribuyente() != 0) {
            Element idFila = documentRecib.createElement("idFilaExcel");
            Text idFilaT = documentRecib.createTextNode(Integer.toString(contribuyente.getIdContribuyente()));
            idFila.appendChild(idFilaT);
            reciboElem.appendChild(idFila);
        } else {
            Element idFila = documentRecib.createElement("idFilaExcel");
            Text idFilaT = documentRecib.createTextNode("");
            idFila.appendChild(idFilaT);
            reciboElem.appendChild(idFila);
        }
        if (contribuyente.getNombre() != null) {
            Element nombre = documentRecib.createElement("nombre");
            Text nombreT = documentRecib.createTextNode(contribuyente.getNombre());
            nombre.appendChild(nombreT);
            reciboElem.appendChild(nombre);
        } else {
            Element nombre = documentRecib.createElement("nombre");
            Text nombreT = documentRecib.createTextNode("");
            nombre.appendChild(nombreT);
            reciboElem.appendChild(nombre);
        }

        if (contribuyente.getApellido1() != null) {
            Element apellido1 = documentRecib.createElement("primerApellido");
            Text apellido1T = documentRecib.createTextNode(contribuyente.getApellido1());
            apellido1.appendChild(apellido1T);
            reciboElem.appendChild(apellido1);
        } else {
            Element apellido1 = documentRecib.createElement("primerApellido");
            Text apellido1T = documentRecib.createTextNode("");
            apellido1.appendChild(apellido1T);
            reciboElem.appendChild(apellido1);
        }

        if (contribuyente.getApellido2() != null) {
            Element apellido2 = documentRecib.createElement("segundoApellido");
            Text apellido2T = documentRecib.createTextNode(contribuyente.getApellido2());
            apellido2.appendChild(apellido2T);
            reciboElem.appendChild(apellido2);
        } else {
            Element apellido2 = documentRecib.createElement("segundoApellido");
            Text apellido2T = documentRecib.createTextNode("");
            apellido2.appendChild(apellido2T);
            reciboElem.appendChild(apellido2);
        }

        if (contribuyente.getNifnie() != null) {
            Element nifnie = documentRecib.createElement("NIF");
            Text nifnieT = documentRecib.createTextNode(contribuyente.getNifnie());
            nifnie.appendChild(nifnieT);
            reciboElem.appendChild(nifnie);
        } else {
            Element nifnie = documentRecib.createElement("NIF");
            Text nifnieT = documentRecib.createTextNode("");
            nifnie.appendChild(nifnieT);
            reciboElem.appendChild(nifnie);
        }

        if (contribuyente.getIban() != null) {
            Element iban = documentRecib.createElement("IBAN");
            Text ibanT = documentRecib.createTextNode(contribuyente.getIban());
            iban.appendChild(ibanT);
            reciboElem.appendChild(iban);
        } else {
            Element iban = documentRecib.createElement("IBAN");
            Text ibanT = documentRecib.createTextNode("");
            iban.appendChild(ibanT);
            reciboElem.appendChild(iban);
        }

        List < String > lecturas = new ArrayList < > (contribuyente.getLecturases());
        if (lecturas.size() == 1) {
            lecturas.add(lecturas.get(0));
        }
        if (Double.parseDouble(lecturas.get(1)) < Double.parseDouble(lecturas.get(0))) {
            String aux = lecturas.get(0);
            lecturas.set(0, lecturas.get(1));
            lecturas.set(1, aux);
        }
        if (lecturas.get(1) != null) {
            Element lecturaAct = documentRecib.createElement("lecturaActual");
            Text lecturaActT = documentRecib.createTextNode(lecturas.get(1));
            lecturaAct.appendChild(lecturaActT);
            reciboElem.appendChild(lecturaAct);
        } else {
            Element lecturaAct = documentRecib.createElement("lecturaActual");
            Text lecturaActT = documentRecib.createTextNode("");
            lecturaAct.appendChild(lecturaActT);
            reciboElem.appendChild(lecturaAct);
        }

        if (lecturas.get(0) != null) {
            Element lecturaAnt = documentRecib.createElement("lecturaAnterior");
            Text lecturaAntT = documentRecib.createTextNode(lecturas.get(0));
            lecturaAnt.appendChild(lecturaAntT);
            reciboElem.appendChild(lecturaAnt);
        } else {
            Element lecturaAnt = documentRecib.createElement("lecturaAnterior");
            Text lecturaAntT = documentRecib.createTextNode("");
            lecturaAnt.appendChild(lecturaAntT);
            reciboElem.appendChild(lecturaAnt);
        }

        if (lecturas.get(0) != null && lecturas.get(1) != null) {
            Element consumo = documentRecib.createElement("consumo");
            Double consumoD = Double.parseDouble(lecturas.get(1)) - Double.parseDouble(lecturas.get(0));
            Text consumoT = documentRecib.createTextNode(consumoD.toString());
            consumo.appendChild(consumoT);
            reciboElem.appendChild(consumo);
        } else {
            Element consumo = documentRecib.createElement("consumo");
            Text consumoT = documentRecib.createTextNode("");
            consumo.appendChild(consumoT);
            reciboElem.appendChild(consumo);
        }

        if (contribuyente.getBaseImponible() != null) {
            Element base = documentRecib.createElement("baseImponibleRecibo");
            Text baseT = documentRecib.createTextNode(df.format(contribuyente.getBaseImponible()));
            base.appendChild(baseT);
            reciboElem.appendChild(base);
        } else {
            Element base = documentRecib.createElement("baseImponibleRecibo");
            Text baseT = documentRecib.createTextNode("");
            base.appendChild(baseT);
            reciboElem.appendChild(base);
        }

        if (contribuyente.getIvaRecibo() != null) {
            Element iva = documentRecib.createElement("ivaRecibo");
            Text ivaT = documentRecib.createTextNode(df.format(contribuyente.getIvaRecibo()));
            iva.appendChild(ivaT);
            reciboElem.appendChild(iva);
        } else {
            Element iva = documentRecib.createElement("ivaRecibo");
            Text ivaT = documentRecib.createTextNode("");
            iva.appendChild(ivaT);
            reciboElem.appendChild(iva);
        }

        if (contribuyente.getIvaRecibo() != null && contribuyente.getBaseImponible() != null) {
            Element total = documentRecib.createElement("totalRecibo");
            Double totalD = contribuyente.getBaseImponible() + contribuyente.getIvaRecibo();
            Text totalT = documentRecib.createTextNode(df.format(totalD));
            total.appendChild(totalT);
            reciboElem.appendChild(total);
        } else {
            Element total = documentRecib.createElement("totalRecibo");
            Text totalT = documentRecib.createTextNode("");
            total.appendChild(totalT);
            reciboElem.appendChild(total);
        }
        recibosElem.appendChild(reciboElem);
    }

    private static Date getLastDayOfTrimestre(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        return calendar.getTime();
    }
}