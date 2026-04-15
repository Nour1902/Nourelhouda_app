package com.example.nourelhoudaapp.DAO;
import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.Utile.Role;
import com.example.nourelhoudaapp.entites.Suiviregles;
import com.example.nourelhoudaapp.entites.Utilisateur;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;

public class UtilisateurDAO implements Serializable {


    public Utilisateur modifier(Utilisateur User) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Utilisateur user =session.merge(User);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
    public Utilisateur findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Utilisateur user = session.get(Utilisateur.class, id);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
    public List<Utilisateur>  getALLUser(){
        Session session= HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx=null;
        try {
            tx= session.beginTransaction();
            List<Utilisateur> users= session.createQuery("from Utilisateur u where u.role = :role", Utilisateur.class).setParameter("role", Role.User).list();
            tx.commit();
            return  users;
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return new ArrayList<Utilisateur>();
        }
    }

    public boolean supprimer(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Utilisateur user = session.get(Utilisateur.class, id);
            if (user != null) session.remove(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

}
