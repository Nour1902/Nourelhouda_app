package com.example.nourelhoudaapp.entites;

import com.example.nourelhoudaapp.Utile.Genre;
import com.example.nourelhoudaapp.Utile.Role;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

import java.io.Serializable;

@Entity
@Table(name = "utilisateur")
public class Utilisateur implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false , length = 100)
  private String nom;

  @Column(nullable = false , length= 100)
  private String prenom;

  @Column(nullable = false ,unique = true, length= 150)
  private String email;

  @Column(nullable = false)
  private String password;

  //mentione comment on peut stocker l'enum dans la BD
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  @Column(nullable = false, length=100)
  private String ville;

  @Column(nullable = true, length=100)
  private String pays = "Algeria";

  @Column (nullable = false )
  private LocalDate dateInscreption=  LocalDate.now();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role=Role.User;

  //les Relations
//user possede plusieur suivi journalier
  @OneToMany(mappedBy="utilisateur" , cascade=CascadeType.ALL , fetch= FetchType.LAZY)
  private List<SuiviJournalier> suivis;

  //user femme possede plus suivi regles
  @OneToMany(mappedBy="utilisateur" , cascade = CascadeType.ALL , fetch= FetchType.LAZY)
  private List<Suiviregles> suiviregles;

  //constrecteur
  public Utilisateur(){}

  public Utilisateur(String nom, String prenom, String email, String password, Genre genre, String ville) {
    this.nom = nom;
    this.prenom = prenom;
    this.email = email;
    this.password = password;
    this.genre = genre;
    this.ville = ville;
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

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Genre getGenre() {
    return genre;
  }

  public void setGenre(Genre genre) {
    this.genre = genre;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  public String getPays() {
    return pays != null ? pays : "Algeria";
  }

  public void setPays(String pays) {
    this.pays = pays;
  }

  public LocalDate getDateInscreption() {
    return dateInscreption;
  }

  public void setDateInscreption(LocalDate dateInscreption) {
    this.dateInscreption = dateInscreption;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public List<SuiviJournalier> getSuivis() {
    return suivis;
  }

  public void setSuivis(List<SuiviJournalier> suivis) {
    this.suivis = suivis;
  }

  public List<Suiviregles> getSuiviRegles() {
    return suiviregles;
  }

  public void setSuiviRegles(List<Suiviregles> suiviRegles) {
    this.suiviregles = suiviRegles;
  }

  @Override
  public String toString(){
    return "Utilisateur{id=" + id + ", nom='" + nom + "', email='" + email + "', role=" + role + "}";
  }
}
