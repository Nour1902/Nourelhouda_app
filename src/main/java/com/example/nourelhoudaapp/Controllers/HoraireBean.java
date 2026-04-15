package com.example.nourelhoudaapp.Controllers;
import com.example.nourelhoudaapp.DAO.HorairePriereDAO;
import com.example.nourelhoudaapp.entites.HorairePriere;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Named("horaireBean")
@SessionScoped
public class HoraireBean implements Serializable {

    private HorairePriere horaire = new HorairePriere();
    private HorairePriereDAO horaireDAO = new HorairePriereDAO();
    private List<HorairePriere> horaires;
    private Boolean deleteSuccess = null;
    private boolean deleteAttempted = false;
    private boolean showForm = false;
    private boolean editMode = false;

    // Champs String pour les heures (JSF ne gère pas LocalTime directement)
    private String fajrStr;
    private String dhuhrStr;
    private String asrStr;
    private String maghribStr;
    private String ishaStr;
    private String dateStr;

    @PostConstruct
    public void init() {
        horaires = horaireDAO.findByVille("Tanger"); // ou findAll si vous l'ajoutez
        deleteSuccess = null;
        deleteAttempted = false;
    }

    public String afficherForm() {
        showForm = true;
        editMode = false;
        horaire = new HorairePriere();
        fajrStr = null; dhuhrStr = null; asrStr = null;
        maghribStr = null; ishaStr = null; dateStr = null;
        return null;
    }

    public String sauvegarder() {
        try {
            // ✅ Parse explicitement avec le format HH:mm
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            horaire.setFajr(LocalTime.parse(fajrStr.trim(), timeFormatter));
            horaire.setDhuhr(LocalTime.parse(dhuhrStr.trim(), timeFormatter));
            horaire.setAsr(LocalTime.parse(asrStr.trim(), timeFormatter));
            horaire.setMaghrib(LocalTime.parse(maghribStr.trim(), timeFormatter));
            horaire.setIsha(LocalTime.parse(ishaStr.trim(), timeFormatter));
            horaire.setDate(LocalDate.parse(dateStr.trim(), dateFormatter));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur", "Format invalide ! Heures: HH:mm (ex: 05:24) — Date: yyyy-MM-dd (ex: 2026-03-15)"));
            return null;
        }

        if (editMode) return modifier();
        else return ajouter();
    }

    public String ajouter() {
        if (horaireDAO.ajouter(horaire)) {
            horaires = horaireDAO.findByVille(horaire.getVille());
            horaire = new HorairePriere();
            showForm = false;
        }
        return null;
    }

    public String modifier() {
        HorairePriere result = horaireDAO.modifier(horaire);
        if (result != null) {
            horaires = horaireDAO.findByVille(horaire.getVille());
            horaire = new HorairePriere();
            showForm = false;
            editMode = false;
        }
        return null;
    }

    public String delete() {
        deleteAttempted = true;
        deleteSuccess = horaireDAO.supprimer(horaire.getId());
        if (deleteSuccess) horaires = horaireDAO.findByVille("Tanger");
        return null;
    }

    public String resetFlags() {
        deleteSuccess = null;
        deleteAttempted = false;
        return null;
    }

    public String prepareEdit() {
        showForm = true;
        editMode = true;
        // Pré-remplir les champs String
        fajrStr     = horaire.getFajr() != null ? horaire.getFajr().toString() : "";
        dhuhrStr    = horaire.getDhuhr() != null ? horaire.getDhuhr().toString() : "";
        asrStr      = horaire.getAsr() != null ? horaire.getAsr().toString() : "";
        maghribStr  = horaire.getMaghrib() != null ? horaire.getMaghrib().toString() : "";
        ishaStr     = horaire.getIsha() != null ? horaire.getIsha().toString() : "";
        dateStr     = horaire.getDate() != null ? horaire.getDate().toString() : "";
        return null;
    }

    public String annulerForm() {
        horaire = new HorairePriere();
        showForm = false;
        editMode = false;
        return null;
    }

    // Getters & Setters
    public HorairePriere getHoraire() { return horaire; }
    public void setHoraire(HorairePriere horaire) { this.horaire = horaire; }
    public List<HorairePriere> getHoraires() { return horaires; }
    public Boolean getDeleteSuccess() { return deleteSuccess; }
    public boolean isDeleteAttempted() { return deleteAttempted; }
    public boolean isShowForm() { return showForm; }
    public void setShowForm(boolean s) { this.showForm = s; }
    public boolean isEditMode() { return editMode; }
    public String getFajrStr() { return fajrStr; }
    public void setFajrStr(String s) { this.fajrStr = s; }
    public String getDhuhrStr() { return dhuhrStr; }
    public void setDhuhrStr(String s) { this.dhuhrStr = s; }
    public String getAsrStr() { return asrStr; }
    public void setAsrStr(String s) { this.asrStr = s; }
    public String getMaghribStr() { return maghribStr; }
    public void setMaghribStr(String s) { this.maghribStr = s; }
    public String getIshaStr() { return ishaStr; }
    public void setIshaStr(String s) { this.ishaStr = s; }
    public String getDateStr() { return dateStr; }
    public void setDateStr(String s) { this.dateStr = s; }
}