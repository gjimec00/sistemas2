/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import base.Contribuyente;
import base.Recibos;

import java.util.List;
import base.HibernateUtil;
import java.util.Scanner;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Guille & Ovi 😎
 */
public class Practicasistemas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Opening session for the database.
        SessionFactory sessionfactory = HibernateUtil.getSessionFactory();
        Session session = sessionfactory.openSession();
        
        //Getting the DNI from the user.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el NIF deseado para mostrar su información: ");
        String NIF = scanner.nextLine();
        //System.out.println(NIF);
        
        String contribuyenteHQL = "FROM Contribuyente c";
        Query queryNIF = session.createQuery(contribuyenteHQL);
        List<Contribuyente> NIFs = queryNIF.list();

        String recibosHQL = "FROM Recibos r";
        Query queryRecNIF = session.createQuery(recibosHQL);
        List<Recibos> recNIFs = queryRecNIF.list();
        
        for(Contribuyente c:NIFs){
            
            if(c.getNifnie().equals(NIF)){
               String nombre = c.getNombre();
               String apellido1 = c.getApellido1();
               String apellido2 = c.getApellido2();
               String nifnie = c.getNifnie();
               String direccion = c.getDireccion();
               System.out.println("Nombre: " + nombre + 
                                  "\nApellidos: " + apellido1 + " " + apellido2 + 
                                  "\nNIF: " + nifnie + 
                                  "\nDireccion: " + direccion);

               for(Recibos r:recNIFs){
                if(nifnie.equals(r.getNifContribuyente())){
                    double totalRec = 250;
                    r.setTotalRecibo(totalRec);
                }
               }
            }
        }
    }

}
