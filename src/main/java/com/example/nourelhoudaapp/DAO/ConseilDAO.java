package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.Conseil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

import java.io.Serializable;

public class ConseilDAO implements Serializable {

    // Ajouter
    public boolean ajouter(Conseil conseil) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(conseil);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Modifier
    public Conseil modifier(Conseil conseil) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Conseil cns =session.merge(conseil);
            tx.commit();
            return cns;
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
            Conseil conseil = session.get(Conseil.class, id);
            if (conseil != null) session.remove(conseil);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Trouver par id
    public Conseil findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Conseil conseil = session.get(Conseil.class, id);
            tx.commit();
            return conseil;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Tous les conseils
    public List<Conseil> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Conseil> liste = session.createQuery(
                    "FROM Conseil", Conseil.class).list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Conseils par genre (Homme / Femme / TOUS)
    public List<Conseil> findByGenre(String genre) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Conseil> liste = session.createQuery(
                            "FROM Conseil WHERE genre = :genre OR genre = 'TOUS'",
                            Conseil.class)
                    .setParameter("genre", genre)
                    .list();
            tx.commit();
            return liste;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Conseils par catégorie
    public List<Conseil> findByCategorie(String categorie) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Conseil> liste = session.createQuery(
                            "FROM Conseil WHERE categorie = :cat", Conseil.class)
                    .setParameter("cat", categorie)
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