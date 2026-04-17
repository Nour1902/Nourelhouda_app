package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.Suiviregles;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

import java.io.Serializable;

public class AuthDAO implements Serializable {

    public static String hachagePassword(String Password) {
        return BCrypt.hashpw(Password, BCrypt.gensalt()); // hashpw() → combine mot de passe + sel pour créer le hash
                                                          // final

    }

    public static boolean verfiyPassword(String Password, String Hash) {
        return BCrypt.checkpw(Password, Hash);
    }

    // fonction pour verfier Email
    public static Long EmailExiste(Session session, String Email) {
        // HQL pour compter le nombre d'utilisateurs avec cet email
        String hql = "SELECT COUNT(u) FROM Utilisateur u WHERE u.email = :emailParam";

        // Exécute la requête et récupère le résultat
        return session.createQuery(hql, Long.class)
                .setParameter("emailParam", Email)
                .uniqueResult();

        // on utilise le meme session et meme transaction car heberneit ne autorise pas
        // 2 transaction dans meme session

    }
    public void validateEmail(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        String emailSaisi = (String) value;
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (emailSaisi == null || !emailSaisi.matches(regex)) {
            FacesMessage message = new FacesMessage("Ce n'est pas un format e-mail");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    // fonction pour inscrire
    public boolean Inscrire(Utilisateur User) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // on a pas de compte avec ce email
            if (EmailExiste(session, User.getEmail()) == 0L) {
                String Hashpassword = hachagePassword(User.getPassword());
                User.setPassword(Hashpassword);
                session.persist(User);
                tx.commit();
                return true;
            } else {
                System.out.println("Email deja existe");
                return false;
            }
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return false;

        }
    }

    // fonction de connexion
    public Utilisateur Connexion(String Email, String Password) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // count=1 signfie que nous avons un et un seul user avec cette email

            Utilisateur user = session.createQuery("from Utilisateur u where u.email = :EmailParam", Utilisateur.class)
                    .setParameter("EmailParam", Email).uniqueResult();
            if (user != null && verfiyPassword(Password, user.getPassword())) {
                tx.commit();
                return user;

            }
            tx.commit();
            return null;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return null;
        }

    }

    public Utilisateur modifier(Utilisateur User) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Utilisateur user = session.merge(User);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

}
