package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.Citation;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

import java.io.Serializable;

public class CitationDAO implements Serializable {

    // Ajouter une citation
    public boolean ajouter(Citation citation) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(citation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Modifier
    public Citation modifier(Citation citation) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
             Citation cita=session.merge(citation);
            tx.commit();
            return cita;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Supprimer
    public boolean supprimer(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Citation citation = session.get(Citation.class, id);
            if (citation != null) session.remove(citation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Trouver par id
    public Citation findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Citation citation = session.get(Citation.class, id);
            tx.commit();
            return citation;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Toutes les citations
    public List<Citation> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Citation> liste = session.createQuery(
                    "FROM Citation", Citation.class).list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Citation du jour (par numéro de jour Ramadan 1-30)
    public Citation findByJour(int jour) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Citation citation = session.createQuery(
                            "FROM Citation WHERE JourRamadan = :jour", Citation.class)
                    .setParameter("jour", jour)
                    .uniqueResult();
            tx.commit();
            return citation;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}