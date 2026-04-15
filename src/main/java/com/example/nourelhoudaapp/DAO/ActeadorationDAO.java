package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.Acteadoration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

import java.io.Serializable;

public class ActeadorationDAO implements Serializable {

    public  boolean Ajouter(Acteadoration acte){
        Session session= HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx= null;
                try{
                    tx= session.beginTransaction();
                    session.persist(acte);
                    tx.commit();
                    return true;
                } catch (Exception e) {
                    if(tx != null) tx.rollback();

                    e.printStackTrace();
                    return false;
                }
    }

    public  Acteadoration Modifer(Acteadoration acte){
        //hibernet qui prend la main pour fermer et ouver la session
        Session session= HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx=null;
        try{
            tx= session.beginTransaction();
            //merge permet de retourner objet mis a joure depuis la base de donne
            Acteadoration acteAdorUpdate=session.merge(acte);
            tx.commit();
            return acteAdorUpdate;
        } catch (Exception e) {
            if(tx != null) tx.rollback();

            e.printStackTrace();
            return  null;
        }
    }

    public boolean Supprimer(Long id){
        Session session=HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx= null;
        try{
            tx= session.beginTransaction();
            //LA methode remove attend un pbjet acte comme parametre
            //on cherche objet acte avec mehde get
            //return null si objet not existe
            Acteadoration acterechercher= session.get(Acteadoration.class,id);
            if(acterechercher != null){
                session.remove(acterechercher);
                tx.commit();
                return  true;
            }else{
                //le cas que acte non trouve
                System.out.println("acte non trouve");
                tx.commit();
                return  false;
            }


        } catch (Exception e) {
            if(tx != null) tx.rollback();

            e.printStackTrace();
            return false;
        }
    }

    public Acteadoration findByID(Long id){
        Session session=HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx= null;
        try{
            tx= session.beginTransaction();
            Acteadoration acte= session.get(Acteadoration.class,id);
            if(acte != null) {
                tx.commit();
                return acte;
            }else{
                System.out.println("Acte non trouve");
                tx.commit();
                return  null;
            }
        } catch (Exception e) {
            if(tx != null) tx.rollback();

            e.printStackTrace();
            return null;
        }
    }
    //recupere all acteadoration

    public List<Acteadoration> getAllACte() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Acteadoration> actes = session.createQuery("from Acteadoration", Acteadoration.class).list();
            tx.commit();
            return actes;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    //recupere all acteadoration par category passer en category

    public List<Acteadoration> findByCategory(String Category){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
        List<Acteadoration> actes= session.createQuery("from Acteadoration a  where a.category = :CategoryParam",Acteadoration.class)
                .setParameter("CategoryParam",Category).list();
        tx.commit();
        return  actes;
    } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
}
}
