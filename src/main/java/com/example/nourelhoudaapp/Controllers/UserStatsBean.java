package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.SuiviJournalierDAO;
import com.example.nourelhoudaapp.entites.SuiviJournalier;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("userStatsBean")
@RequestScoped
public class UserStatsBean implements Serializable {

    @Inject
    private AuthBean authBean;

    private SuiviJournalierDAO suiviDAO = new SuiviJournalierDAO();

    private int joursSuivis   = 0;
    private int moyenneScore  = 0;
    private int meilleurScore = 0;
    private int regularite    = 0;

    /**
     * 30-slot array indexed by Ramadan day (index 0 = Day 1, index 29 = Day 30).
     * Each slot holds the total scoreJour accumulated on that Ramadan day.
     */
    private List<Integer> scoresParJour = new ArrayList<>();

    @PostConstruct
    public void init() {
        Utilisateur user = authBean.getUser();
        if (user == null) return;

        List<SuiviJournalier> allSuivis = suiviDAO.findByUtilisateur(user.getId());
        
        // Group total scoreJour by gregorian date (only "fait" records)
        Map<LocalDate, Integer> scoresMap = allSuivis == null ? Map.of() : allSuivis.stream()
                .filter(SuiviJournalier::getFait)
                .collect(Collectors.groupingBy(
                        SuiviJournalier::getDate,
                        Collectors.summingInt(SuiviJournalier::getScoreJour)));

        joursSuivis   = scoresMap.size();

        if (joursSuivis > 0) {
            moyenneScore  = (int) scoresMap.values().stream().mapToInt(Integer::intValue).average().orElse(0);
            meilleurScore = scoresMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        }

        // Build a 30-slot array indexed by Ramadan day (1–30)
        Integer[] slots = new Integer[30];
        Arrays.fill(slots, 0);

        // Determine the current Hijri date
        HijrahDate today    = HijrahDate.now();
        int currentHijrMonth = today.get(ChronoField.MONTH_OF_YEAR);
        int currentHijrDay   = today.get(ChronoField.DAY_OF_MONTH);

        for (Map.Entry<LocalDate, Integer> entry : scoresMap.entrySet()) {
            LocalDate gDate = entry.getKey();
            try {
                HijrahDate hDate   = HijrahDate.from(gDate);
                int hijrahMonth    = hDate.get(ChronoField.MONTH_OF_YEAR);
                int hijrahDay      = hDate.get(ChronoField.DAY_OF_MONTH);

                // Add score to the slot if it's in the CURRENT Hijri month
                if (hijrahMonth == currentHijrMonth && hijrahDay >= 1 && hijrahDay <= 30) {
                    slots[hijrahDay - 1] += entry.getValue();
                }
            } catch (Exception e) {
                // Skip invalid dates
            }
        }

        // We display days up to the current day of the month so the user sees their progress this month
        int dayLimit = currentHijrDay;

        // For demo purposes, if outside Ramadan and we want to see the chart, 
        // we can still render it. It's safe since dayLimit will be 30.
        scoresParJour = new ArrayList<>();
        for (int i = 0; i < dayLimit; i++) {
            scoresParJour.add(slots[i]);
        }

        // Régularité: % of Ramadan days tracked so far (max dayLimit)
        if (dayLimit > 0) {
            regularite = (int) Math.round(((double) joursSuivis / dayLimit) * 100);
        }
    }

    public int getJoursSuivis()             { return joursSuivis; }
    public int getMoyenneScore()            { return moyenneScore; }
    public int getMeilleurScore()           { return meilleurScore; }
    public int getRegularite()              { return regularite; }
    public List<Integer> getScoresParJour() { return scoresParJour; }
}
