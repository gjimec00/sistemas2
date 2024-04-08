/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practicasistemas;
import java.util.Scanner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Guille & Ovi 😎
 */
public class Practicasistemas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Getting the DNI from the user.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el NIF deseado para mostrar su información: ");
        String NIF = scanner.nextLine();
        System.out.println(NIF);
    
        //Configure and connect to the Hibernate session
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        
        session.close();
    }

}
