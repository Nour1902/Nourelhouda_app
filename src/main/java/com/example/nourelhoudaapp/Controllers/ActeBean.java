package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.ActeadorationDAO;
import com.example.nourelhoudaapp.entites.Acteadoration;
import com.example.nourelhoudaapp.Utile.Category;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("acteBean")
@SessionScoped
public class ActeBean implements Serializable {

    private Acteadoration acte = new Acteadoration();
    private ActeadorationDAO acteDAO = new ActeadorationDAO();
    private List<Acteadoration> actes;
    private Boolean deleteSuccess = null;
    private boolean deleteAttempted = false;
    private boolean showForm = false;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        actes = acteDAO.getAllACte();
        deleteSuccess = null;
        deleteAttempted = false;
    }

    public String afficherForm() {
        showForm = true;
        editMode = false;
        acte = new Acteadoration();
        return null;
    }

    public String sauvegarder() {
        if (editMode) return modifier();
        else return ajouter();
    }

    public String ajouter() {
        if (acteDAO.Ajouter(acte)) {
            actes = acteDAO.getAllACte();
            acte = new Acteadoration();
            showForm = false;
        }
        return null;
    }

    public String modifier() {
        Acteadoration result = acteDAO.Modifer(acte);
        if (result != null) {
            actes = acteDAO.getAllACte();
            acte = new Acteadoration();
            showForm = false;
            editMode = false;
        }
        return null;
    }

    public String delete() {
        deleteAttempted = true;
        deleteSuccess = acteDAO.Supprimer(acte.getId());
        if (deleteSuccess) actes = acteDAO.getAllACte();
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
        acte = new Acteadoration();
        showForm = false;
        editMode = false;
        return null;
    }

    // Getters & Setters
    public Acteadoration getActe() { return acte; }
    public void setActe(Acteadoration acte) { this.acte = acte; }
    public List<Acteadoration> getActes() { return actes; }
    public Boolean getDeleteSuccess() { return deleteSuccess; }
    public boolean isDeleteAttempted() { return deleteAttempted; }
    public boolean isShowForm() { return showForm; }
    public void setShowForm(boolean showForm) { this.showForm = showForm; }
    public boolean isEditMode() { return editMode; }
    public Category[] getCategories() { return Category.values(); }
}