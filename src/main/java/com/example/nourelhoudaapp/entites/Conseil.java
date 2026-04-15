package com.example.nourelhoudaapp.entites;

import com.example.nourelhoudaapp.Utile.CategoryConseil;
import com.example.nourelhoudaapp.Utile.Genre;
import jakarta.persistence.*;

@Entity
@Table(name = "conseils")
public class Conseil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryConseil categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre = Genre.TOUS;

    @Column(name = "pour_ramadan", nullable = false, columnDefinition = "boolean default false")
    private boolean pourRamadan = false;

    // Enums

    // Constructeurs
    public Conseil() {}

    public Conseil(String titre, String contenu, CategoryConseil categorie, Genre genre, boolean pourRamadan) {
        this.titre       = titre;
        this.contenu     = contenu;
        this.categorie   = categorie;
        this.genre       = genre;
        this.pourRamadan = pourRamadan;
    }

    // Getters & Setters
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getTitre()                     { return titre; }
    public void setTitre(String titre)           { this.titre = titre; }

    public String getContenu()                   { return contenu; }
    public void setContenu(String contenu)       { this.contenu = contenu; }

    public CategoryConseil getCategorie()              { return categorie; }
    public void setCategorie(CategoryConseil c)        { this.categorie = c; }

    public Genre getGenre()                      { return genre; }
    public void setGenre(Genre genre)            { this.genre = genre; }

    public boolean isPourRamadan()               { return pourRamadan; }
    public void setPourRamadan(boolean value)    { this.pourRamadan = value; }

    @Override
    public String toString() {
        return "Conseil{id=" + id + ", titre='" + titre + "', categorie=" + categorie + ", genre=" + genre + "}";
    }
}