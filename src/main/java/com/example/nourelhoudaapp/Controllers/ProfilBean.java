package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.UtilisateurDAO;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.mindrot.jbcrypt.BCrypt;
import com.example.nourelhoudaapp.Services.CityCountryService;

import java.io.Serializable;

@Named("profilBean")
@SessionScoped
public class ProfilBean implements Serializable {

    @Inject
    private AuthBean authBean; // ✅ récupère l'utilisateur connecté

    @Inject
    private CityCountryService cityCountryService;

    private UtilisateurDAO userDAO = new UtilisateurDAO();

    private String prenom;
    private String nom;
    private String email;
    private String ville;
    private String pays;
    private String ancienPassword;
    private String nouveauPassword;
    private String confirmerPassword;
    private boolean updateSuccess = false;

    @PostConstruct
    public void init() {
        // ✅ Pré-remplir avec les données actuelles
        Utilisateur u = authBean.getUser();
        prenom = u.getPrenom();
        nom    = u.getNom();
        email  = u.getEmail();
        ville  = u.getVille();
        pays   = u.getPays();
    }

    public String sauvegarder() {
        updateSuccess = false;
        Utilisateur u = authBean.getUser();
        System.out.println(u.getId());
        // ✅ Recharger l'utilisateur depuis la BD pour avoir le password
        Utilisateur uFromDB = userDAO.findById(u.getId());
        if (uFromDB == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur", "Utilisateur introuvable !"));
            return null;
        }

        // ✅ Mettre à jour les infos de base
        uFromDB.setPrenom(prenom);
        uFromDB.setNom(nom);
        uFromDB.setEmail(email);

        // Update country automatically if city changes
        if(ville != null && !ville.equals(u.getVille())) {
            uFromDB.setVille(ville);
            pays = cityCountryService.getCountry(ville);
            System.out.println("[ProfilBean] Ville modifée: " + ville + " → Pays: " + pays);
        } else {
            uFromDB.setVille(ville);
        }
        uFromDB.setPays(pays);

        // ✅ Changer le mot de passe SEULEMENT si nouveauPassword est rempli
        if (nouveauPassword != null && !nouveauPassword.trim().isEmpty()) {

            if (ancienPassword == null || ancienPassword.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erreur", "Veuillez saisir l'ancien mot de passe !"));
                return null;
            }

            // ✅ Vérifier avec le password de la BD
            if (!BCrypt.checkpw(ancienPassword, uFromDB.getPassword())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erreur", "Ancien mot de passe incorrect !"));
                return null;
            }

            if (!nouveauPassword.equals(confirmerPassword)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erreur", "Les mots de passe ne correspondent pas !"));
                return null;
            }

            uFromDB.setPassword(BCrypt.hashpw(nouveauPassword, BCrypt.gensalt()));
        }

        // ✅ Sauvegarder en BD
        Utilisateur result = userDAO.modifier(uFromDB);
        if (result != null) {
            authBean.setUser(result);
            updateSuccess = true;
            ancienPassword = null;
            nouveauPassword = null;
            confirmerPassword = null;
        }

        return null;
    }

    // Getters & Setters
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getAncienPassword() { return ancienPassword; }
    public void setAncienPassword(String s) { this.ancienPassword = s; }
    public String getNouveauPassword() { return nouveauPassword; }
    public void setNouveauPassword(String s) { this.nouveauPassword = s; }
    public String getConfirmerPassword() { return confirmerPassword; }
    public void setConfirmerPassword(String s) { this.confirmerPassword = s; }
    public boolean isUpdateSuccess() { return updateSuccess; }
}
