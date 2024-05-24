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


/**
 *
 * @author Guille & Ovi 😎
 */
public class DBManager {

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
            //contribuyentesBBDD.get(0).setNombre(contribuyente.getNombre());
            //contribuyentesBBDD.get(0).setNifnie(contribuyente.getNifnie());
            contribuyentesBBDD.get(0).setDireccion(contribuyente.getDireccion());
            contribuyentesBBDD.get(0).setNumero(contribuyente.getNumero());
            contribuyentesBBDD.get(0).setPaisCcc(contribuyente.getPaisCcc());
            contribuyentesBBDD.get(0).setCcc(contribuyente.getCcc());
            contribuyentesBBDD.get(0).setIban(contribuyente.getIban());
            contribuyentesBBDD.get(0).setEemail(contribuyente.getEemail());
            contribuyentesBBDD.get(0).setExencion(contribuyente.getExencion());
            contribuyentesBBDD.get(0).setBonificacion((double) contribuyente.getBonificacion());
            //contribuyentesBBDD.get(0).setFechaAlta(contribuyente.getFechaAlta());
            contribuyentesBBDD.get(0).setFechaBaja(contribuyente.getFechaBaja());

            session.update(contribuyentesBBDD.get(0));

            auxiliar = contribuyentesBBDD.get(0);
        
        }
        tx.commit();
        
    }
    /*
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

            auxiliar.setId(linea.getId());
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

            lineasreciboBBDD.get(0).setId(linea.getId());
            //auxiliar.setConcepto(linea.getConcepto());
            //auxiliar.setSubconcepto(linea.getSubconcepto());
            lineasreciboBBDD.get(0).setBaseImponible(linea.getBaseImponible());
            lineasreciboBBDD.get(0).setPorcentajeIva(linea.getPorcentajeIva());
            lineasreciboBBDD.get(0).setImporteiva(linea.getImporteiva());
            lineasreciboBBDD.get(0).setM3incluidos(linea.getM3incluidos());
            lineasreciboBBDD.get(0).setBonificacion(linea.getBonificacion());
            lineasreciboBBDD.get(0).setImporteBonificacion(linea.getImporteBonificacion());
            //auxiliar.setIdRecibo(linea.getIdRecibo);

            session.update(lineasreciboBBDD.get(0));
        
        }
        tx.commit();
        
    }*/
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

            auxiliar.setId(lastIdOrd);
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
            //ordenanzasBBDD.get(0).setId(lastIdOrd);
            //ordenanzasBBDD.get(0).setIdOrdenanza(ordenanza.getIdOrdenanza());
            //ordenanzasBBDD.get(0).setConcepto(ordenanza.getConcepto());
            //ordenanzasBBDD.get(0).setSubconcepto(ordenanza.getSubconcepto());
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
    }
}
