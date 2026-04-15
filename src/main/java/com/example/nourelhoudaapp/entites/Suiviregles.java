package com.example.nourelhoudaapp.entites;
import jakarta.persistence.*;
import java.time.LocalDate;

import java.io.Serializable;

@Entity
@Table(name = "suivi_regles")
public class Suiviregles implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Constructeurs
    public Suiviregles() {}

    public Suiviregles(Utilisateur utilisateur, LocalDate dateDebut, LocalDate dateFin, String notes) {
        this.utilisateur = utilisateur;
        this.dateDebut   = dateDebut;
        this.dateFin     = dateFin;
        this.notes       = notes;
    }

    // Getters & Setters
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public Utilisateur getUtilisateur()          { return utilisateur; }
    public void setUtilisateur(Utilisateur u)    { this.utilisateur = u; }

    public LocalDate getDateDebut()              { return dateDebut; }
    public void setDateDebut(LocalDate d)        { this.dateDebut = d; }

    public LocalDate getDateFin()                { return dateFin; }
    public void setDateFin(LocalDate d)          { this.dateFin = d; }

    public String getNotes()                     { return notes; }
    public void setNotes(String notes)           { this.notes = notes; }

    @Override
    public String toString() {
        return "SuiviRegles{id=" + id + ", debut=" + dateDebut + ", fin=" + dateFin + "}";
    }
}