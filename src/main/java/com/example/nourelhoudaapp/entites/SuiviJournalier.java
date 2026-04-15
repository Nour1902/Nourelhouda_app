package com.example.nourelhoudaapp.entites;

import jakarta.persistence.*;

import java.time.LocalDate;

import java.io.Serializable;

@Entity
@Table(name="suivi_journalier", uniqueConstraints=@UniqueConstraint(columnNames = {"utilisateur_id" , "acte_adoration_id" , "date"}))
public class SuiviJournalier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   //on mentione la colonne au bd qui va etre column de jointure
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name="utilisateur_id" , nullable = false)
   private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="acte_adoration_id" , nullable = false)
    private Acteadoration acteadoration;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean  fait;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer quantite = 0;

    @Column(nullable = false , name="score_jour")
    private Integer scoreJour=0;

     public SuiviJournalier(){}

     public SuiviJournalier(Utilisateur utilisateur, Acteadoration acteadoration, LocalDate date, Boolean fait, Integer quantite, Integer scoreJour) {
        this.utilisateur = utilisateur;
        this.acteadoration = acteadoration;
        this.date = date;
        this.fait = fait;
        this.quantite = quantite != null ? quantite : 0;
        this.scoreJour = scoreJour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Acteadoration getActeadoration() {
        return acteadoration;
    }

    public void setActeadoration(Acteadoration acteadoration) {
        this.acteadoration = acteadoration;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getFait() {
        return fait;
    }

    public void setFait(Boolean fait) {
        this.fait = fait;
    }

    public Integer getScoreJour() {
        return scoreJour;
    }

    public void setScoreJour(Integer scoreJour) {
        this.scoreJour = scoreJour;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "SuiviJournalier{id=" + id + ", date=" + date + ", fait=" + fait + ", score=" + scoreJour + "}";
    }
}
