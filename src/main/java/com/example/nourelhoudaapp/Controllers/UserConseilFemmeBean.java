package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.ConseilDAO;
import com.example.nourelhoudaapp.entites.Conseil;
import com.example.nourelhoudaapp.entites.Utilisateur;
import com.example.nourelhoudaapp.entites.Suiviregles;
import com.example.nourelhoudaapp.DAO.SuivireglesDAO;
import com.example.nourelhoudaapp.Utile.CategoryConseil;
import com.example.nourelhoudaapp.Utile.Genre;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Named("userConseilFemmeBean")
@RequestScoped
public class UserConseilFemmeBean implements Serializable {

    @Inject
    private AuthBean authBean;

    @Inject
    private UserDashboardBean userDashboardBean;

    private ConseilDAO conseilDAO = new ConseilDAO();

    private List<Conseil> motifsFemme;
    private List<Conseil> allConseilsValides;
    
    private boolean enPeriodeRegles = false;
    private SuivireglesDAO suiviDAO = new SuivireglesDAO();

    @PostConstruct
    public void init() {
        Utilisateur user = authBean.getUser();
        Genre userGenre = (user != null) ? user.getGenre() : Genre.TOUS;

        // Check if user is currently on her period
        if (user != null && String.valueOf(userGenre).equalsIgnoreCase("FEMME")) {
            List<Suiviregles> historique = suiviDAO.findByUtilisatrice(user.getId());
            LocalDate today = LocalDate.now();
            if (historique != null) {
                enPeriodeRegles = historique.stream().anyMatch(s -> {
                    boolean started = s.getDateDebut().isBefore(today) || s.getDateDebut().isEqual(today);
                    boolean ended = s.getDateFin() != null && s.getDateFin().isBefore(today);
                    return started && !ended;
                });
            }
        }

        List<Conseil> tousConseils = conseilDAO.findAll();

        if (tousConseils != null) {
            // Filtrer les conseils applicables à l'utilisateur selon son genre et sa période de règles
            allConseilsValides = tousConseils.stream()
                    .filter(c -> {
                        boolean isForTous = String.valueOf(c.getGenre()).equalsIgnoreCase("TOUS");
                        boolean isForUser = String.valueOf(c.getGenre()).equalsIgnoreCase(String.valueOf(userGenre));
                        
                        if (!isForTous && !isForUser) return false;
                        
                        // Ramadan Filter
                        if (c.isPourRamadan() && !userDashboardBean.isRamadan()) return false;

                        boolean isSpeciallyForGirl = String.valueOf(c.getGenre()).equalsIgnoreCase("FEMME") || CategoryConseil.REGLES == c.getCategorie();
                        
                        if (isSpeciallyForGirl) {
                            return enPeriodeRegles;
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
                    
            // On peut garder motifsFemme pour compatibilité si nécessaire
            motifsFemme = allConseilsValides.stream()
                    .filter(c -> CategoryConseil.REGLES == c.getCategorie() || String.valueOf(c.getGenre()).equalsIgnoreCase("FEMME"))
                    .collect(Collectors.toList());
        }
    }

    public boolean isEnPeriodeRegles() { return enPeriodeRegles; }

    public List<Conseil> getMotifsFemme() { return motifsFemme; }
    public List<Conseil> getAllConseilsValides() { return allConseilsValides; }

    public String getBadgeColor(String category) {
        if (category == null) return "#e6e6e6"; // DEFAULT
        switch (category.toUpperCase()) {
            case "NUTRITION": return "#e5dfcf"; 
            case "FEMME": case "REGLES": return "#f8c8d4";
            case "SPIRITUEL": return "#dcefe3";
            case "SPORT": return "#e0e6ed";
            default: return "#f0f0f0";
        }
    }
}
