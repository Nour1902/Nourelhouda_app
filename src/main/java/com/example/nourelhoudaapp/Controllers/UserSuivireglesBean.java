package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.SuivireglesDAO;
import com.example.nourelhoudaapp.entites.Suiviregles;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Named("userSuivireglesBean")
@ViewScoped
public class UserSuivireglesBean implements Serializable {

    @Inject
    private AuthBean authBean;

    private SuivireglesDAO suiviDAO = new SuivireglesDAO();
    private List<Suiviregles> historique;
    
    // Form fields
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String notes;

    @PostConstruct
    public void init() {
        loadHistorique();
    }

    public void loadHistorique() {
        Utilisateur user = authBean.getUser();
        if (user != null) {
            historique = suiviDAO.findByUtilisatrice(user.getId());
        }
    }

    public void enregistrerCycle() {
        Utilisateur user = authBean.getUser();
        if (user == null || dateDebut == null) return;
        
        // If end date is not filled, default it to start date (1 day cycle) or leave it null.
        // It's better to store it if the user only had one day, but the entity permits null dateFin.
        Suiviregles nouveau = new Suiviregles(user, dateDebut, dateFin, notes);
        suiviDAO.ajouter(nouveau);
        
        // Reset form
        dateDebut = null;
        dateFin = null;
        notes = null;
        
        loadHistorique();
    }

    public void supprimerCycle(Long id) {
        suiviDAO.supprimer(id);
        loadHistorique();
    }

    // Transform History into a simple JSON array structure to be injected in Javascript
    // Format: [{"start": "YYYY-MM-DD", "end": "YYYY-MM-DD"}, ...]
    public String getCyclesJson() {
        if (historique == null || historique.isEmpty()) {
            return "[]";
        }
        
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < historique.size(); i++) {
            Suiviregles s = historique.get(i);
            String startStr = s.getDateDebut().format(df);
            String endStr = s.getDateFin() != null ? s.getDateFin().format(df) : startStr;
            
            json.append("{");
            json.append("\"id\": ").append(s.getId()).append(",");
            json.append("\"start\": \"").append(startStr).append("\",");
            json.append("\"end\": \"").append(endStr).append("\"");
            json.append("}");
            
            if (i < historique.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    // Getters and Setters
    public List<Suiviregles> getHistorique() { return historique; }
    public void setHistorique(List<Suiviregles> historique) { this.historique = historique; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
