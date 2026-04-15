package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.HorairePriere;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;

import java.io.Serializable;

public class HorairePriereDAO implements Serializable {

    // Ajouter
    public boolean ajouter(HorairePriere horaire) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(horaire);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Modifier
    public HorairePriere modifier(HorairePriere horaire) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            HorairePriere HP = session.merge(horaire);
            tx.commit();
            return HP;
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
            HorairePriere horaire = session.get(HorairePriere.class, id);
            if (horaire != null) session.remove(horaire);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Horaire par ville et date
    public HorairePriere findByVilleEtDate(String ville, LocalDate date) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            HorairePriere horaire = session.createQuery(
                    "FROM HorairePriere WHERE ville = :ville AND date = :date",
                    HorairePriere.class)
                    .setParameter("ville", ville)
                    .setParameter("date", date)
                    .uniqueResult();
            tx.commit();
            return horaire;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Tous les horaires d'une ville
    public List<HorairePriere> findByVille(String ville) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<HorairePriere> liste = session.createQuery(
                    "FROM HorairePriere WHERE ville = :ville", HorairePriere.class)
                    .setParameter("ville", ville)
                    .list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Tous les horaires
    public List<HorairePriere> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<HorairePriere> liste = session.createQuery("FROM HorairePriere", HorairePriere.class).list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}