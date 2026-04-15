package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.SuiviJournalier;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;

import java.io.Serializable;

public class SuiviJournalierDAO implements Serializable {

    // Ajouter un suivi
    public boolean ajouter(SuiviJournalier suivi) {
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
    public SuiviJournalier modifier(SuiviJournalier suivi) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SuiviJournalier suiviJournalier =session.merge(suivi);
            tx.commit();
            return suiviJournalier;
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
            SuiviJournalier suivi = session.get(SuiviJournalier.class, id);
            if (suivi != null) session.remove(suivi);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Tous les suivis d'un utilisateur
    public List<SuiviJournalier> findByUtilisateur(Long utilisateurId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<SuiviJournalier> liste = session.createQuery(
                    //HQL on utilise les objets java avec leur attribus sans les getters et les setters et ici
                    //utilisateur.id  se taruit en utilisateur_id  dans la table SuiviJournalier dans la base de donne
                            "FROM SuiviJournalier WHERE utilisateur.id = :uid",
                            SuiviJournalier.class)
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

    // Suivi d'un utilisateur pour une date précise
    public List<SuiviJournalier> findByUtilisateurEtDate(Long utilisateurId, LocalDate date) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<SuiviJournalier> liste = session.createQuery(
                            "FROM SuiviJournalier WHERE utilisateur.id = :uid AND date = :date",
                            SuiviJournalier.class)
                    .setParameter("uid", utilisateurId)
                    .setParameter("date", date)
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