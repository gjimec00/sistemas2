/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicasistemas;

import java.text.SimpleDateFormat;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import POJOS.Contribuyente;
import POJOS.HibernateUtil;
import POJOS.Lineasrecibo;
import POJOS.Recibos;
import POJOS.Ordenanza;
import POJOS.Lecturas;
import POJOS.RelContribuyenteOrdenanza;


/**
 *
 * @author Guille & Ovi 😎
 */
public class DBManager {

    /**
     * Guarda la información del contribuyente en la BB DD.
     *
     * @param contribuyente Es el contribuyente objetivo a guardar.
     */
    
    public static void saveContribuyente(Contribuyente contribuyente){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String contribuyenteHQL = "FROM Contribuyente c WHERE c.nifnie = :arg1 AND c.nombre = :arg2 AND c.fechaAlta = :arg3";
        Query contribuyenteQuery = session.createQuery(contribuyenteHQL);
        contribuyenteQuery.setParameter("arg1", contribuyente.getNifnie());
        contribuyenteQuery.setParameter("arg2", contribuyente.getNombre());
        contribuyenteQuery.setParameter("arg3", contribuyente.getFechaAlta());
        
        List<Contribuyente> contribuyentesBBDD = contribuyenteQuery.list();
        Contribuyente auxiliar = new Contribuyente();

        if(contribuyentesBBDD.isEmpty()){
            String lastContHQL = "SELECT COALESCE(MAX(c.idContribuyente), 0) FROM Contribuyente c";
            Query lastContQuery = session.createQuery(lastContHQL);
            
            int lastIdCont = (int) lastContQuery.uniqueResult() + 1;

            auxiliar.setIdContribuyente(contribuyente.getIdContribuyente());
            auxiliar.setNombre(contribuyente.getNombre());
            auxiliar.setApellido1(contribuyente.getApellido1());
            auxiliar.setApellido2(contribuyente.getApellido2());
            auxiliar.setNifnie(contribuyente.getNifnie());
            auxiliar.setDireccion(contribuyente.getDireccion());
            auxiliar.setNumero(contribuyente.getNumero());
            auxiliar.setPaisCcc(contribuyente.getPaisCcc());
            auxiliar.setCcc(contribuyente.getCcc());
            auxiliar.setIban(contribuyente.getIban());
            auxiliar.setEemail(contribuyente.getEemail());
            auxiliar.setExencion(contribuyente.getExencion());
            auxiliar.setBonificacion(contribuyente.getBonificacion());
            auxiliar.setFechaAlta(contribuyente.getFechaAlta());
            contribuyente.setFechaBaja(contribuyente.getFechaBaja());
            
            session.save(auxiliar);
        
        }else{
            contribuyentesBBDD.get(0).setApellido1(contribuyente.getApellido1());
            contribuyentesBBDD.get(0).setApellido2(contribuyente.getApellido2());
            contribuyentesBBDD.get(0).setDireccion(contribuyente.getDireccion());
            contribuyentesBBDD.get(0).setNumero(contribuyente.getNumero());
            contribuyentesBBDD.get(0).setPaisCcc(contribuyente.getPaisCcc());
            contribuyentesBBDD.get(0).setCcc(contribuyente.getCcc());
            contribuyentesBBDD.get(0).setIban(contribuyente.getIban());
            contribuyentesBBDD.get(0).setEemail(contribuyente.getEemail());
            contribuyentesBBDD.get(0).setExencion(contribuyente.getExencion());
            contribuyentesBBDD.get(0).setBonificacion((double) contribuyente.getBonificacion());
            contribuyentesBBDD.get(0).setFechaBaja(contribuyente.getFechaBaja());

            session.update(contribuyentesBBDD.get(0));

            auxiliar = contribuyentesBBDD.get(0);
        
        }
        tx.commit();
        session.close();
        
    }

    /**
     * Guarda la información de las líneas de recibo en la BB DD.
     *
     * @param linea Es la línea de recibo objetivo a guardar.
     */
    
    public static void saveLineasRecibo(Lineasrecibo linea){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String lineasreciboHQL = "FROM Lineasrecibo lr WHERE lr.concepto = :arg1 AND lr.subconcepto = :arg2 AND lr.recibos = :arg3";
        Query lineasreciboQuery = session.createQuery(lineasreciboHQL);
        lineasreciboQuery.setParameter("arg1", linea.getConcepto());
        lineasreciboQuery.setParameter("arg2", linea.getSubconcepto());
        lineasreciboQuery.setParameter("arg3", linea.getRecibos());

        List<Lineasrecibo> lineasreciboBBDD = lineasreciboQuery.list();
        Lineasrecibo auxiliar = new Lineasrecibo();

        if(lineasreciboBBDD.isEmpty()){
            String lastContHQL = "SELECT COALESCE(MAX(lr.id), 0) FROM Lineasrecibo lr";
            Query lastContQuery = session.createQuery(lastContHQL);
            
            int lastIdCont = (int) lastContQuery.uniqueResult() + 1;

            auxiliar.setId(lastIdCont);
            auxiliar.setConcepto(linea.getConcepto());
            auxiliar.setSubconcepto(linea.getSubconcepto());
            auxiliar.setBaseImponible(linea.getBaseImponible());
            auxiliar.setPorcentajeIva(linea.getPorcentajeIva());
            auxiliar.setImporteiva(linea.getImporteiva());
            auxiliar.setM3incluidos(linea.getM3incluidos());
            auxiliar.setBonificacion(linea.getBonificacion());
            auxiliar.setImporteBonificacion(linea.getImporteBonificacion());
            auxiliar.setRecibos(linea.getRecibos());

            session.save(auxiliar);
        }else{
            String lastContHQL = "SELECT COALESCE(MAX(lr.id), 0) FROM Lineasrecibo lr";
            Query lastContQuery = session.createQuery(lastContHQL);
            
            int lastIdCont = (int) lastContQuery.uniqueResult() + 1;

            lineasreciboBBDD.get(0).setBaseImponible(linea.getBaseImponible());
            lineasreciboBBDD.get(0).setPorcentajeIva(linea.getPorcentajeIva());
            lineasreciboBBDD.get(0).setImporteiva(linea.getImporteiva());
            lineasreciboBBDD.get(0).setM3incluidos(linea.getM3incluidos());
            lineasreciboBBDD.get(0).setBonificacion(linea.getBonificacion());
            lineasreciboBBDD.get(0).setImporteBonificacion(linea.getImporteBonificacion());

            session.update(lineasreciboBBDD.get(0));
        
        }
        tx.commit();
        session.close();
        
    }

    /**
     * Guarda la información de las ordenanzas en la BB DD.
     *
     * @param ordenanza Es la ordenanza objetivo a guardar.
     */
    
    public static void saveOrdenanzas(Ordenanza ordenanza){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String ordenanzaHQL = "FROM Ordenanza o WHERE o.idOrdenanza = :arg1 AND o.concepto = :arg2 AND o.subconcepto = :arg3";
        Query ordenanzaQuery = session.createQuery(ordenanzaHQL);
        ordenanzaQuery.setParameter("arg1", ordenanza.getIdOrdenanza());
        ordenanzaQuery.setParameter("arg2", ordenanza.getConcepto());
        ordenanzaQuery.setParameter("arg3", ordenanza.getSubconcepto());
        
        List<Ordenanza> ordenanzasBBDD = ordenanzaQuery.list();
        Ordenanza auxiliar = new Ordenanza();

        if(ordenanzasBBDD.isEmpty()){
            String lastOrdHQL = "SELECT COALESCE(MAX(o.id), 0) FROM Ordenanza o";
            Query lastOrdQuery = session.createQuery(lastOrdHQL);
            
            int lastIdOrd = (int) lastOrdQuery.uniqueResult() + 1;

            auxiliar.setId(ordenanza.getId());
            auxiliar.setIdOrdenanza(ordenanza.getIdOrdenanza());
            auxiliar.setConcepto(ordenanza.getConcepto());
            auxiliar.setSubconcepto(ordenanza.getSubconcepto());
            auxiliar.setDescripcion(ordenanza.getDescripcion());
            auxiliar.setAcumulable(ordenanza.getAcumulable());
            auxiliar.setPrecioFijo(ordenanza.getPrecioFijo());
            auxiliar.setM3incluidos(ordenanza.getM3incluidos());
            auxiliar.setPreciom3(ordenanza.getPreciom3());
            auxiliar.setPorcentaje(ordenanza.getPorcentaje());
            auxiliar.setConceptoRelacionado(ordenanza.getConceptoRelacionado());
            auxiliar.setIva(ordenanza.getIva());
            auxiliar.setPueblo(ordenanza.getPueblo());
            auxiliar.setTipoCalculo(ordenanza.getTipoCalculo());

            session.save(auxiliar);
        }else{
            ordenanzasBBDD.get(0).setDescripcion(ordenanza.getDescripcion());
            ordenanzasBBDD.get(0).setAcumulable(ordenanza.getAcumulable());
            ordenanzasBBDD.get(0).setPrecioFijo(ordenanza.getPrecioFijo());
            ordenanzasBBDD.get(0).setM3incluidos(ordenanza.getM3incluidos());
            ordenanzasBBDD.get(0).setPreciom3(ordenanza.getPreciom3());
            ordenanzasBBDD.get(0).setPorcentaje(ordenanza.getPorcentaje());
            ordenanzasBBDD.get(0).setConceptoRelacionado(ordenanza.getConceptoRelacionado());
            ordenanzasBBDD.get(0).setIva(ordenanza.getIva());
            ordenanzasBBDD.get(0).setPueblo(ordenanza.getPueblo());
            ordenanzasBBDD.get(0).setTipoCalculo(ordenanza.getTipoCalculo());

            session.update(ordenanzasBBDD.get(0));
        }
        tx.commit();
        session.close();
    }

    /**
     * Guarda la información de los recibos en la BB DD.
     *
     * @param recibo Es el recibo objetivo a guardar.
     */

    public static int saveRecibos(Recibos recibo){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String recibosHQL = "FROM Recibos r WHERE r.nifContribuyente = :arg1 AND r.fechaPadron = :arg2";
        Query recibosQuery = session.createQuery(recibosHQL);
        recibosQuery.setParameter("arg1", recibo.getNifContribuyente());
        recibosQuery.setParameter("arg2", recibo.getFechaPadron());

        List<Recibos> recibosBBDD = recibosQuery.list();
        Recibos auxiliar = new Recibos();

        if(recibosBBDD.isEmpty()){
            String lastRecHQL = "SELECT COALESCE(MAX(r.numeroRecibo), 0) FROM Recibos r";
            Query lastRecQuery = session.createQuery(lastRecHQL);

            int lastIdRec = (int) lastRecQuery.uniqueResult() + 1;

            auxiliar.setNumeroRecibo(lastIdRec);
            auxiliar.setContribuyente(recibo.getContribuyente());
            auxiliar.setNifContribuyente(recibo.getNifContribuyente());
            auxiliar.setDireccionCompleta(recibo.getDireccionCompleta());
            auxiliar.setNombre(recibo.getNombre());
            auxiliar.setApellidos(recibo.getApellidos());
            auxiliar.setFechaRecibo(recibo.getFechaRecibo());
            auxiliar.setLecturaAnterior(recibo.getLecturaAnterior());
            auxiliar.setLecturaActual(recibo.getLecturaActual());
            auxiliar.setConsumom3(recibo.getConsumom3());
            auxiliar.setFechaPadron(recibo.getFechaPadron());
            auxiliar.setTotalBaseImponible(recibo.getTotalBaseImponible());
            auxiliar.setTotalIva(recibo.getTotalIva());
            auxiliar.setTotalRecibo(recibo.getTotalRecibo());
            auxiliar.setIban(recibo.getIban());
            auxiliar.setExencion(recibo.getExencion());

            session.save(auxiliar);
        }else{
            recibosBBDD.get(0).setContribuyente(recibo.getContribuyente());
            recibosBBDD.get(0).setNifContribuyente(recibo.getNifContribuyente());
            recibosBBDD.get(0).setDireccionCompleta(recibo.getDireccionCompleta());
            recibosBBDD.get(0).setNombre(recibo.getNombre());
            recibosBBDD.get(0).setApellidos(recibo.getApellidos());
            recibosBBDD.get(0).setFechaRecibo(recibo.getFechaRecibo());
            recibosBBDD.get(0).setLecturaAnterior(recibo.getLecturaAnterior());
            recibosBBDD.get(0).setLecturaActual(recibo.getLecturaActual());
            recibosBBDD.get(0).setConsumom3(recibo.getConsumom3());
            recibosBBDD.get(0).setFechaPadron(recibo.getFechaPadron());
            recibosBBDD.get(0).setTotalBaseImponible(recibo.getTotalBaseImponible());
            recibosBBDD.get(0).setTotalIva(recibo.getTotalIva());
            recibosBBDD.get(0).setTotalRecibo(recibo.getTotalRecibo());
            recibosBBDD.get(0).setIban(recibo.getIban());
            recibosBBDD.get(0).setExencion(recibo.getExencion());

            session.update(recibosBBDD.get(0));
            auxiliar = recibosBBDD.get(0);
        }
        tx.commit();
        session.close();
        return auxiliar.getNumeroRecibo();
    }

    /**
     * Guarda la información de las lecturas en la BB DD.
     *
     * @param lectura Es la lectura objetivo a guardar.
     */
    
    public static void saveLecturas(Lecturas lectura){
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String lecturasHQL = "FROM Lecturas l WHERE l.contribuyente = :arg1 AND l.ejercicio = :arg2 AND l.periodo = :arg3";
        Query lecturasQuery = session.createQuery(lecturasHQL);
        lecturasQuery.setParameter("arg1", lectura.getContribuyente());
        lecturasQuery.setParameter("arg2", lectura.getEjercicio());
        lecturasQuery.setParameter("arg3", lectura.getPeriodo());

        List<Lecturas> lecturasBBDD = lecturasQuery.list();
        Lecturas auxiliar = new Lecturas();

        if(lecturasBBDD.isEmpty()){
            String lastLecHQL = "SELECT COALESCE(MAX(l.id), 0) FROM Lecturas l";
            Query lastLecQuery = session.createQuery(lastLecHQL);

            int lastIdLec = (int) lastLecQuery.uniqueResult() + 1;

            auxiliar.setId(lastIdLec);
            auxiliar.setContribuyente(lectura.getContribuyente());
            auxiliar.setEjercicio(lectura.getEjercicio());
            auxiliar.setPeriodo(lectura.getPeriodo());
            auxiliar.setLecturaAnterior(lectura.getLecturaAnterior());
            auxiliar.setLecturaActual(lectura.getLecturaActual());

            session.save(auxiliar);
        }else{
            lecturasBBDD.get(0).setContribuyente(lectura.getContribuyente());
            lecturasBBDD.get(0).setEjercicio(lectura.getEjercicio());
            lecturasBBDD.get(0).setPeriodo(lectura.getPeriodo());
            lecturasBBDD.get(0).setLecturaAnterior(lectura.getLecturaAnterior());
            lecturasBBDD.get(0).setLecturaActual(lectura.getLecturaActual());

            session.update(lecturasBBDD.get(0));
        }
        tx.commit();
        session.close();
    }

    /**
     * Guarda la información de la relación entre contribuyente y ordenanza en la BB DD.
     *
     * @param relConOrd Es la relación objetivo a guardar.
     */
    
    public static void saveRelCon(RelContribuyenteOrdenanza relConOrd){
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();

            SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
            String relConOrdHQL = "FROM RelContribuyenteOrdenanza r WHERE r.id = :arg1 AND r.contribuyente = :arg2 AND r.ordenanza = :arg3";
            Query relConOrdQuery = session.createQuery(relConOrdHQL);
            relConOrdQuery.setParameter("arg1", relConOrd.getId());
            relConOrdQuery.setParameter("arg2", relConOrd.getContribuyente());
            relConOrdQuery.setParameter("arg3", relConOrd.getOrdenanza());

            List<RelContribuyenteOrdenanza> relConOrdBBDD = relConOrdQuery.list();
            RelContribuyenteOrdenanza auxiliar = new RelContribuyenteOrdenanza();

            if(relConOrdBBDD.isEmpty()){
                String lastRelConOrdHQL = "SELECT COALESCE(MAX(r.id), 0) FROM RelContribuyenteOrdenanza r";
                Query lastRelConOrdQuery = session.createQuery(lastRelConOrdHQL);

                int lastIdRec = (int) lastRelConOrdQuery.uniqueResult() + 1;

                auxiliar.setId(lastIdRec);
                auxiliar.setContribuyente(relConOrd.getContribuyente());
                auxiliar.setOrdenanza(relConOrd.getOrdenanza());

                session.save(auxiliar);
            }else{
                relConOrdBBDD.get(0).setContribuyente(relConOrd.getContribuyente());
                relConOrdBBDD.get(0).setOrdenanza(relConOrd.getOrdenanza());

                session.update(relConOrdBBDD.get(0));
            }
            tx.commit();
            session.close();
        }
}
