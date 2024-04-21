/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import POJOS.Contribuyente;
import POJOS.Recibos;

import java.util.List;
import POJOS.HibernateUtil;
import java.util.Scanner;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
        Transaction tx = session.beginTransaction();
        
        //Getting the DNI from the user.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el NIF deseado para mostrar su información: ");
        String NIF = scanner.nextLine();

        boolean find = false;
        String contribuyenteHQL = "FROM Contribuyente c";
        Query queryNIF = session.createQuery(contribuyenteHQL);
        List<Contribuyente> NIFs = queryNIF.list();

        String recibosHQL = "FROM Recibos r";
        Query queryRecNIF = session.createQuery(recibosHQL);
        List<Recibos> recNIFs = queryRecNIF.list();

        String lineasReciboHQL = "SELECT AVG(baseImponible) FROM Lineasrecibo";
        Query querylineasRecibo = session.createQuery(lineasReciboHQL);
        Double mediaBaseImp = (Double) querylineasRecibo.uniqueResult();

        for(Contribuyente c:NIFs){
            
            if(c.getNifnie().equals(NIF)){
               find = true;
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
               try {
               String eliminarHQL = "DELETE FROM Lineasrecibo WHERE baseImponible < :media";
               Query queryEliminar = session.createQuery(eliminarHQL);
               queryEliminar.setParameter("media", mediaBaseImp);
               queryEliminar.executeUpdate();
               } catch (Exception e) {
                e.printStackTrace();
               }
            }
        }
        
        if(find == false){
            System.out.println("El trabajador no se ha encontrado en el sistema.");
        }
        tx.commit();
        HibernateUtil.shutdown();
    }

}
