package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.ConseilDAO;
import com.example.nourelhoudaapp.entites.Conseil;
import com.example.nourelhoudaapp.Utile.CategoryConseil;
import com.example.nourelhoudaapp.Utile.Genre;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("conseilBean")
@SessionScoped
public class ConseilBean implements Serializable {

    private Conseil conseil = new Conseil();
    private ConseilDAO conseilDAO = new ConseilDAO();
    private List<Conseil> conseils;
    private Boolean deleteSuccess = null;
    private boolean deleteAttempted = false;
    private boolean showForm = false;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        conseils = conseilDAO.findAll();
        deleteSuccess = null;
        deleteAttempted = false;
    }

    public String afficherForm() {
        showForm = true;
        editMode = false;
        conseil = new Conseil();
        return null;
    }

    public String sauvegarder() {
        if (editMode) return modifier();
        else return ajouter();
    }

    public String ajouter() {
        if (conseilDAO.ajouter(conseil)) {
            conseils = conseilDAO.findAll();
            conseil = new Conseil();
            showForm = false;
        }
        return null;
    }

    public String modifier() {
        Conseil result = conseilDAO.modifier(conseil);
        if (result != null) {
            conseils = conseilDAO.findAll();
            conseil = new Conseil();
            showForm = false;
            editMode = false;
        }
        return null;
    }

    public String delete() {
        deleteAttempted = true;
        deleteSuccess = conseilDAO.supprimer(conseil.getId());
        if (deleteSuccess) conseils = conseilDAO.findAll();
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
        return null;
    }

    public String annulerForm() {
        conseil = new Conseil();
        showForm = false;
        editMode = false;
        return null;
    }

    // Getters & Setters
    public Conseil getConseil() { return conseil; }
    public void setConseil(Conseil conseil) { this.conseil = conseil; }
    public List<Conseil> getConseils() { return conseils; }
    public Boolean getDeleteSuccess() { return deleteSuccess; }
    public boolean isDeleteAttempted() { return deleteAttempted; }
    public boolean isShowForm() { return showForm; }
    public void setShowForm(boolean s) { this.showForm = s; }
    public boolean isEditMode() { return editMode; }
    public CategoryConseil[] getCategories() { return CategoryConseil.values(); }
    public Genre[] getGenres() { return Genre.values(); }
}