/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;

import base.newHibernateUtil;
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
        SessionFactory sessionfactory = newHibernateUtil.getSessionFactory();
        Session session = sessionfactory.openSession();
        
        //Getting the DNI from the user.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el NIF deseado para mostrar su información: ");
        String NIF = scanner.nextLine();
        System.out.println(NIF);
        
        String contribuyenteSQL = "FROM Contribuyente c";
        Query queryNIF = session.createQuery(contribuyenteSQL);
    }

}
