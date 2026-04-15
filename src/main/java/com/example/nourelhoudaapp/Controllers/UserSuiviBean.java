package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.ActeadorationDAO;
import com.example.nourelhoudaapp.DAO.SuiviJournalierDAO;
import com.example.nourelhoudaapp.Utile.Category;
import com.example.nourelhoudaapp.entites.Acteadoration;
import com.example.nourelhoudaapp.entites.SuiviJournalier;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;



import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Named("userSuiviBean")
@RequestScoped
public class UserSuiviBean implements Serializable {
    @Inject
    private AuthBean authBean;

    private ActeadorationDAO acteDAO = new ActeadorationDAO();
    private SuiviJournalierDAO suiviDAO = new SuiviJournalierDAO();

    private List<Acteadoration> adorations;
    private List<Acteadoration> prieres;
    private List<Acteadoration> dhikrs;
    private List<Acteadoration> nuits;

    private List<SuiviJournalier> suivisToday;

    private int scoreActuel = 0;
    private int scoreMax = 0;

    @PostConstruct
    public void init() {
        List<Acteadoration> tousActes = acteDAO.getAllACte();

        if (tousActes == null || tousActes.isEmpty()) {
            return;
        }

        scoreMax = tousActes.stream()
                .mapToInt(Acteadoration::getPoints).sum();

        adorations = tousActes.stream()
                .filter(a -> a.getCategory() == Category.ADORATION)
                .collect(Collectors.toList());

        prieres = tousActes.stream()
                .filter(a -> a.getCategory() == Category.PRIERE)
                .collect(Collectors.toList());

        dhikrs = tousActes.stream()
                .filter(a -> a.getCategory() == Category.DHIKR)
                .collect(Collectors.toList());
                
        nuits = tousActes.stream()
                .filter(a -> a.getCategory() == Category.NUIT)
                .collect(Collectors.toList());
                
        loadSuivisForToday();
    }
    
    private void loadSuivisForToday() {
        Utilisateur user = authBean.getUser();
        if (user == null) return;
        
        suivisToday = suiviDAO.findByUtilisateurEtDate(user.getId(), LocalDate.now());

        
        scoreActuel = 0;
        
        if (suivisToday != null) {
            scoreActuel += suivisToday.stream()
                    .filter(SuiviJournalier::getFait)
                    .mapToInt(SuiviJournalier::getScoreJour)
                    .sum();
        }
        

    }



    public boolean isFait(Long acteId) {
        if (suivisToday == null) return false;
        return suivisToday.stream()
                .anyMatch(s -> s.getActeadoration().getId().equals(acteId) && s.getFait());
    }


    public void toggleAction(Long acteId) {
        Utilisateur user = authBean.getUser();
        if (user == null) return;

        Acteadoration acte = acteDAO.findByID(acteId);
        if (acte == null) return;

        Optional<SuiviJournalier> optSuivi = suivisToday != null ? suivisToday.stream()
                .filter(s -> s.getActeadoration().getId().equals(acteId))
                .findFirst() : Optional.empty();

        if (optSuivi.isPresent()) {
            SuiviJournalier suivi = optSuivi.get();
            suivi.setFait(!suivi.getFait());
            suivi.setScoreJour(suivi.getFait() ? acte.getPoints() : 0);
            suiviDAO.modifier(suivi);
        } else {
            SuiviJournalier nouveauSuivi = new SuiviJournalier(user, acte, LocalDate.now(), true, 0, acte.getPoints());
            suiviDAO.ajouter(nouveauSuivi);
        }
        
        loadSuivisForToday();
    }

    public List<Acteadoration> getAdorations() { return adorations; }
    public List<Acteadoration> getPrieres() { return prieres; }
    public List<Acteadoration> getDhikrs() { return dhikrs; }
    public List<Acteadoration> getNuits() { return nuits; }
    public int getScoreActuel() { return scoreActuel; }
    public int getScoreMax() { return scoreMax; }
    public String getFormattedDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH));
    }
    

}
