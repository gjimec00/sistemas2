package base;
// Generated 31-mar-2024 14:13:27 by Hibernate Tools 4.3.1


/**
 * Lecturas generated by hbm2java
 */
public class Lecturas  implements java.io.Serializable {


     private int id;
     private Contribuyente contribuyente;
     private String ejercicio;
     private String periodo;
     private int lecturaAnterior;
     private int lecturaActual;

    public Lecturas() {
    }

    public Lecturas(int id, Contribuyente contribuyente, String ejercicio, String periodo, int lecturaAnterior, int lecturaActual) {
       this.id = id;
       this.contribuyente = contribuyente;
       this.ejercicio = ejercicio;
       this.periodo = periodo;
       this.lecturaAnterior = lecturaAnterior;
       this.lecturaActual = lecturaActual;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public Contribuyente getContribuyente() {
        return this.contribuyente;
    }
    
    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }
    public String getEjercicio() {
        return this.ejercicio;
    }
    
    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }
    public String getPeriodo() {
        return this.periodo;
    }
    
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    public int getLecturaAnterior() {
        return this.lecturaAnterior;
    }
    
    public void setLecturaAnterior(int lecturaAnterior) {
        this.lecturaAnterior = lecturaAnterior;
    }
    public int getLecturaActual() {
        return this.lecturaActual;
    }
    
    public void setLecturaActual(int lecturaActual) {
        this.lecturaActual = lecturaActual;
    }




}


