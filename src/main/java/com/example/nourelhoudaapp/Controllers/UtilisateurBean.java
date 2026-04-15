package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.UtilisateurDAO;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("utilisateurBean")
@RequestScoped
public class UtilisateurBean implements Serializable {
    private Utilisateur user;
    private UtilisateurDAO userDAO;
    private List<Utilisateur> users;
    private Boolean deleteSuccess = null;
    private boolean deleteAttempted = false;
    public UtilisateurBean(){
        user=new Utilisateur();
        userDAO= new UtilisateurDAO();
        users=userDAO.getALLUser();
    }

    @PostConstruct  // ✅ appelé à chaque nouveau chargement de page
    public void init() {
        users = userDAO.getALLUser();
        deleteSuccess = null;      // ✅ reset
        deleteAttempted = false;   // ✅ reset
    }
    public Utilisateur getUser() {
        return user;
    }

    public void setUser(Utilisateur user) {
        this.user = user;
    }

    public List<Utilisateur> getUsers() {
        return users;
    }

    public void setUsers(List<Utilisateur> users) {
        this.users = users;
    }

    public Boolean getDeleteSuccess() {
        return deleteSuccess;
    }

    public boolean isDeleteAttempted() {
        return deleteAttempted;
    }

    public String delete(){
        deleteAttempted = true;
        //ici on veux afficher popup de confermation avant supprission
       if( userDAO.supprimer(user.getId())){
           //ici popup pour affiche succesion de supprision de users
           deleteSuccess = true;
           users = userDAO.getALLUser();

       }else {
           deleteSuccess = false;

       }return  null; //pour retser dans page d'affichage des users

    }

}
