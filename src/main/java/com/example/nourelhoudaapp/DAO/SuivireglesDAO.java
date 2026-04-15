package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.Suiviregles;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;

import java.io.Serializable;

public class SuivireglesDAO implements Serializable {

    // Ajouter
    public boolean ajouter(Suiviregles suivi) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(suivi);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Modifier
    public Suiviregles modifier(Suiviregles suivi) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Suiviregles suiviregles =session.merge(suivi);
            tx.commit();
            return suiviregles;
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
            Suiviregles suivi = session.get(Suiviregles.class, id);
            if (suivi != null) session.remove(suivi);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Tous les suivis d'une utilisatrice
    public List<Suiviregles> findByUtilisatrice(Long utilisateurId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Suiviregles> liste = session.createQuery(
                            "FROM Suiviregles WHERE utilisateur.id = :uid",
                            Suiviregles.class)
                    .setParameter("uid", utilisateurId)
                    .list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Suivi entre deux dates
    public List<Suiviregles> findByUtilisatriceEtPeriode(Long utilisateurId,
                                                         LocalDate debut,
                                                         LocalDate fin) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Suiviregles> liste = session.createQuery(
                            "FROM Suiviregles WHERE utilisateur.id = :uid AND dateDebut =  :debut AND dateFin = :fin",
                            Suiviregles.class)
                    .setParameter("uid", utilisateurId)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}