package com.example.nourelhoudaapp.entites;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name="acte_personnel")
public class ActePersonnel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_acte", nullable = false)
    private TypeActePersonnel typeActe;

    @Column(length = 255)
    private String description;

    @Column
    private Integer quantite;

    @Column(name = "points_obtenus", nullable = false)
    private Integer pointsObtenus = 0;

    public ActePersonnel() {}

    public ActePersonnel(Utilisateur utilisateur, LocalDate date, TypeActePersonnel typeActe, String description, Integer quantite, Integer pointsObtenus) {
        this.utilisateur = utilisateur;
        this.date = date;
        this.typeActe = typeActe;
        this.description = description;
        this.quantite = quantite;
        this.pointsObtenus = pointsObtenus != null ? pointsObtenus : 0;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TypeActePersonnel getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(TypeActePersonnel typeActe) {
        this.typeActe = typeActe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Integer getPointsObtenus() {
        return pointsObtenus;
    }

    public void setPointsObtenus(Integer pointsObtenus) {
        this.pointsObtenus = pointsObtenus;
    }
}
