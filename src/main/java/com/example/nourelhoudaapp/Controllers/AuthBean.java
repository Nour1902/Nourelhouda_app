package com.example.nourelhoudaapp.Controllers;
import com.example.nourelhoudaapp.DAO.AuthDAO;
import com.example.nourelhoudaapp.DAO.UtilisateurDAO;
import com.example.nourelhoudaapp.entites.Utilisateur;
import com.example.nourelhoudaapp.Services.CityCountryService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.example.nourelhoudaapp.Utile.Role;

import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {
    private Utilisateur user;
    private final AuthDAO authDAO;

    @Inject
    private CityCountryService cityCountryService;

    public Utilisateur getUser() {
        return user;
    }

    public void setUser(Utilisateur user) {
        this.user = user;
    }

    //constrecteur
    public AuthBean(){
        user=new Utilisateur();
        authDAO=new AuthDAO();
    }

    public String Connecter() {
        Utilisateur utilisateurConnecter = authDAO.Connexion(user.getEmail(), user.getPassword());
        if (utilisateurConnecter != null) {
            // ✅ Sauvegarder l'utilisateur connecté dans la session
            this.user = utilisateurConnecter;

            // ✅ Rediriger selon le rôle
            if (utilisateurConnecter.getRole() == Role.Admin) {
                return "/EspaceAdmin/dashboardAdmin.xhtml?faces-redirect=true";
            } else {
                return "/EspaceUser/dashboardUser.xhtml?faces-redirect=true";
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur", "Email ou mot de passe incorrect"));
            return null;
        }
    }

    public String Inscrire() {

        // ✅ Sauvegarder les données du formulaire
        Utilisateur newUser = new Utilisateur();
        newUser.setNom(user.getNom());
        newUser.setPrenom(user.getPrenom());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setGenre(user.getGenre());
        newUser.setVille(user.getVille());
        if ("admin@nourelhouda.com".equals(user.getEmail())) {
            newUser.setRole(Role.Admin);
        } else {
            newUser.setRole(Role.User);
        }

        // Auto-detect country from city via Nominatim
        String pays = cityCountryService.getCountry(user.getVille());
        newUser.setPays(pays);
        System.out.println("[AuthBean] Ville: " + user.getVille() + " → Pays: " + pays);

        boolean res = authDAO.Inscrire(newUser); // ✅ nouvel objet sans id

        if (res) {
            user = new Utilisateur(); // ✅ reset user
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Inscription réussie"));
            return "/Auth/login.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur", "Email déjà utilisé"));
            return null;
        }
    }
    public String Deconnecter() {
        // ✅ Invalider la session pour vider le bean
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
        return "/home.xhtml?faces-redirect=true";
    }

}
