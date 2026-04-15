package com.example.nourelhoudaapp.entites;

import jakarta.persistence.*;
import com.example.nourelhoudaapp.Utile.Category;
import java.util.List;

import java.io.Serializable;

@Entity
@Table(name="Acteadoration")
public class Acteadoration implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Column (nullable = false , length=100)
    private String nom;

   @Column (name="nom_arabe" , length=150)
    private String nomArabe;

   @Column (nullable = false)
    private Integer points;

   @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;



   //Relations

    @OneToMany(mappedBy = "acteadoration" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<SuiviJournalier> suivis;

    public Acteadoration(){}
    public Acteadoration(String nom, String nomArabe, Integer points, Category category) {
        this.nom = nom;
        this.nomArabe = nomArabe;
        this.points = points;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNomArabe() {
        return nomArabe;
    }

    public void setNomArabe(String nomArabe) {
        this.nomArabe = nomArabe;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }



    public List<SuiviJournalier> getSuivis() {
        return suivis;
    }

    public void setSuivis(List<SuiviJournalier> suivis) {
        this.suivis = suivis;
    }
    @Override
    public String toString() {
        return "ActeAdoration{id=" + id + ", nom='" + nom + "', points=" + points + ", categorie=" + category + "}";
    }
}
