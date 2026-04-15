package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.CitationDAO;
import com.example.nourelhoudaapp.entites.Citation;
import com.example.nourelhoudaapp.Utile.Type;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("citationBean")
@SessionScoped
public class CitationBean implements Serializable {

    private Citation citation = new Citation();
    private CitationDAO citationDAO = new CitationDAO();
    private List<Citation> citations;
    private Boolean deleteSuccess = null;
    private boolean deleteAttempted = false;
    private boolean showForm = false;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        citations = citationDAO.findAll();
    }
    public String afficherForm() {
        showForm = true;
        editMode = false;
        citation = new Citation(); // formulaire vide

        return null;

    }
    public String sauvegarder() {
        if (editMode) {
            return modifier();
        } else {
            return ajouter();
        }
    }

    // ── DELETE ──
    public String delete() {
        deleteAttempted = true;
        deleteSuccess = citationDAO.supprimer(citation.getId());
        if (deleteSuccess) citations = citationDAO.findAll();
        return null;
    }

    // ── AJOUTER ──
    public String ajouter() {
        if (citationDAO.ajouter(citation)) {
            citations = citationDAO.findAll();
            citation = new Citation();
            showForm = false;
        }
        return null;
    }

    // ── MODIFIER ──
    public String modifier() {
        Citation result = citationDAO.modifier(citation);
        if (result != null) {
            citations = citationDAO.findAll();
            citation = new Citation();
            showForm = false;
            editMode = false;
        }
        return null;
    }
    public String resetFlags() {
        deleteSuccess = null;
        deleteAttempted = false;
        return null;
    }
    // ── PRÉPARER ÉDITION ──
    public String prepareEdit() {
        showForm = true;
        editMode = true;

        return null;
    }

    // ── ANNULER FORM ──
    public String annulerForm() {
        citation = new Citation();
        showForm = false;
        editMode = false;
        return null;
    }

    // ── GETTERS / SETTERS ──
    public Citation getCitation() { return citation; }
    public void setCitation(Citation citation) { this.citation = citation; }
    public List<Citation> getCitations() { return citations; }
    public Boolean getDeleteSuccess() { return deleteSuccess; }
    public boolean isDeleteAttempted() { return deleteAttempted; }
    public boolean isShowForm() { return showForm; }
    public void setShowForm(boolean showForm) { this.showForm = showForm; }
    public boolean isEditMode() { return editMode; }
    public Type[] getTypes() { return Type.values(); }
}