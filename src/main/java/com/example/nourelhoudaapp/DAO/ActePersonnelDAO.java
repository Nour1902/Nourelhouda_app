package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.ActePersonnel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class ActePersonnelDAO {

    public void ajouter(ActePersonnel acte) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(acte);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void supprimer(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ActePersonnel acte = session.get(ActePersonnel.class, id);
            if (acte != null) {
                session.remove(acte);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<ActePersonnel> findByUtilisateurEtDate(Long utilisateurId, LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActePersonnel> query = session.createQuery(
                    "FROM ActePersonnel a WHERE a.utilisateur.id = :utilisateurId AND a.date = :date",
                    ActePersonnel.class
            );
            query.setParameter("utilisateurId", utilisateurId);
            query.setParameter("date", date);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
