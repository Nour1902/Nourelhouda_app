package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.ActeadorationDAO;
import com.example.nourelhoudaapp.DAO.CitationDAO;
import com.example.nourelhoudaapp.DAO.SuiviJournalierDAO;
import com.example.nourelhoudaapp.entites.Citation;
import com.example.nourelhoudaapp.entites.HorairePriere;
import com.example.nourelhoudaapp.entites.Utilisateur;
import com.example.nourelhoudaapp.Services.PrayerTimeService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Named("userDashboardBean")
@RequestScoped
public class UserDashboardBean implements Serializable {

    @Inject
    private AuthBean authBean;

    private CitationDAO citationDAO = new CitationDAO();
    @Inject
    private PrayerTimeService prayerTimeService;
    private SuiviJournalierDAO suiviDAO = new SuiviJournalierDAO();
    private ActeadorationDAO acteDAO = new ActeadorationDAO();

    private Citation citationDuJour;
    private HorairePriere horaireAujourdhui;
    private int jourRamadan = 1;
    private int scoreDuJour = 0;
    private int totalActesFaits = 0;
    private int totalActes = 0;
    private int moyenneScore = 0;
    private int meilleurJour = 0;
    private String nextEventName = "Iftar";
    private String nextEventTimeStr = "";
    private long countdownSeconds = 0;
    private int hijriYear = 1447;
    private int hijriDay  = 1;
    private String hijriMonthAr = "رَمَضَان";
    private boolean ramadan = false;

    @PostConstruct
    public void init() {
        Utilisateur user = authBean.getUser();
        if (user == null)
            return;

        // Jour Ramadan via AlAdhan API (par ville + pays de l'utilisateur)
        String ville = user.getVille() != null ? user.getVille() : "Alger";
        String pays  = user.getPays()  != null ? user.getPays()  : "Algeria";

        int jourRamadanApi = prayerTimeService.getJourRamadan(ville, pays);
        if (jourRamadanApi > 0) {
            jourRamadan = jourRamadanApi;
            ramadan = true;
        } else {
            jourRamadan = 1;
            ramadan = false;
        }
        hijriYear   = prayerTimeService.getHijriYear(ville, pays);
        hijriDay    = prayerTimeService.getHijriDay(ville, pays);
        hijriMonthAr = prayerTimeService.getHijriMonthAr(ville, pays);

        // Fetch Citation of the day
        List<Citation> toutesCitations = citationDAO.findAll();
        if (!toutesCitations.isEmpty()) {
            citationDuJour = toutesCitations.stream()
                    .filter(c -> c.getJourRamadan() == jourRamadan)
                    .findFirst()
                    .orElse(toutesCitations.get(0));
        }

        // Horaires de prière (passe ville + pays)
        horaireAujourdhui = prayerTimeService.getHoraireDuJour(ville, pays);
        if (horaireAujourdhui != null) {
            calculateTimers();
        }

        // Fetch today's actual tracking data
        List<com.example.nourelhoudaapp.entites.SuiviJournalier> suivisToday = suiviDAO
                .findByUtilisateurEtDate(user.getId(), LocalDate.now());
        if (suivisToday != null) {
            scoreDuJour = suivisToday.stream().filter(com.example.nourelhoudaapp.entites.SuiviJournalier::getFait)
                    .mapToInt(com.example.nourelhoudaapp.entites.SuiviJournalier::getScoreJour).sum();
            totalActesFaits = (int) suivisToday.stream()
                    .filter(com.example.nourelhoudaapp.entites.SuiviJournalier::getFait).count();
        }

        List<com.example.nourelhoudaapp.entites.Acteadoration> allActes = acteDAO.getAllACte();
        if (allActes != null) {
            totalActes = allActes.size();
        }

        // Fetch user stats
        List<com.example.nourelhoudaapp.entites.SuiviJournalier> allSuivis = suiviDAO.findByUtilisateur(user.getId());
        if (allSuivis != null && !allSuivis.isEmpty()) {
            java.util.Map<LocalDate, Integer> scoresParJour = allSuivis.stream()
                    .filter(com.example.nourelhoudaapp.entites.SuiviJournalier::getFait)
                    .collect(java.util.stream.Collectors.groupingBy(
                            com.example.nourelhoudaapp.entites.SuiviJournalier::getDate,
                            java.util.stream.Collectors
                                    .summingInt(com.example.nourelhoudaapp.entites.SuiviJournalier::getScoreJour)));

            if (!scoresParJour.isEmpty()) {
                moyenneScore = (int) scoresParJour.values().stream().mapToInt(Integer::intValue).average().orElse(0);
                meilleurJour = scoresParJour.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            }
        }
    }

    private void calculateTimers() {
        LocalTime now = LocalTime.now();
        LocalTime iftarTime = horaireAujourdhui.getMaghrib();
        LocalTime imsakTime = horaireAujourdhui.getImsak();

        // For simplicity, pretend Iftar is coming next
        if (now.isBefore(iftarTime) && now.isAfter(imsakTime)) {
            nextEventName = "Iftar";
            nextEventTimeStr = iftarTime.toString();
            countdownSeconds = ChronoUnit.SECONDS.between(now, iftarTime);
        } else {
            nextEventName = "Imsak";
            nextEventTimeStr = imsakTime.toString();
            countdownSeconds = ChronoUnit.SECONDS.between(now, imsakTime);
            if (countdownSeconds < 0) {
                countdownSeconds += 24 * 3600; // Ajoute 24h en secondes si le temps est pour demain
            }
        }

        // Ensure non-negative
        if (countdownSeconds < 0)
            countdownSeconds = 0;
    }

    public int getJourRamadan()    { return jourRamadan; }
    public int getHijriYear()       { return hijriYear; }
    public int getHijriDay()        { return hijriDay; }
    public String getHijriMonthAr() { return hijriMonthAr; }
    public boolean isRamadan()      { return ramadan; }

    /** Single pre-formatted date string for the badge — avoids JSF encoding issues. */
    public String getHijriDateBadge() {
        if (ramadan) {
            return "Jour " + jourRamadan + " \u2014 Ramadan " + hijriYear + "H";
        } else {
            return hijriDay + " " + hijriMonthAr + " " + hijriYear + "H";
        }
    }

    public Citation getCitationDuJour() {
        return citationDuJour;
    }

    public HorairePriere getHoraireAujourdhui() {
        return horaireAujourdhui;
    }

    public int getScoreDuJour() {
        return scoreDuJour;
    }

    public int getTotalActesFaits() {
        return totalActesFaits;
    }

    public int getTotalActes() {
        return totalActes;
    }

    public String getNextEventName() {
        return nextEventName;
    }

    public String getNextEventTimeStr() {
        return nextEventTimeStr;
    }

    public String getFormattedCountdown() {
        long hours = countdownSeconds / 3600;
        long minutes = (countdownSeconds % 3600) / 60;
        long seconds = countdownSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public int getMoyenneScore() {
        return moyenneScore;
    }

    public int getMeilleurJour() {
        return meilleurJour;
    }

    public int getProgressPercentage() {
        if (totalActes == 0)
            return 0;
        // In prototype, max score was 118, but we'll adapt dynamically if needed.
        // Or simply base on max point sum:
        // We will just do percentage based on score vs max possible score if known, or
        // totalActesFaits vs totalActes
        return (int) Math.round(((double) totalActesFaits / totalActes) * 100);
    }

}
