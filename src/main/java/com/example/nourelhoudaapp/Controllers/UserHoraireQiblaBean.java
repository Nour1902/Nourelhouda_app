package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.Services.PrayerTimeService;
import com.example.nourelhoudaapp.entites.HorairePriere;
import com.example.nourelhoudaapp.entites.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalTime;

@Named("userHoraireQiblaBean")
@RequestScoped
public class UserHoraireQiblaBean implements Serializable {

    @Inject
    private AuthBean authBean;

    @Inject
    private PrayerTimeService prayerTimeService;
    
    private HorairePriere horaireAujourdhui;
    private String ville = "Alger";
    private String nextPrayer = "";

    @PostConstruct
    public void init() {
        Utilisateur user = authBean.getUser();
        if (user != null && user.getVille() != null) {
            ville = user.getVille();
        }

        String pays = (authBean.getUser() != null && authBean.getUser().getPays() != null)
                ? authBean.getUser().getPays() : "Algeria";

        horaireAujourdhui = prayerTimeService.getHoraireDuJour(ville, pays);
        
        if (horaireAujourdhui != null) {
            computeNextPrayer();
        }
    }

    private void computeNextPrayer() {
        LocalTime now = LocalTime.now();
        LocalTime imsak   = horaireAujourdhui.getImsak();
        LocalTime fajr    = horaireAujourdhui.getFajr();
        LocalTime dhuhr   = horaireAujourdhui.getDhuhr();
        LocalTime asr     = horaireAujourdhui.getAsr();
        LocalTime maghrib = horaireAujourdhui.getMaghrib();
        LocalTime isha    = horaireAujourdhui.getIsha();

        if (now.isBefore(imsak)) {
            nextPrayer = "IMSAK";
        } else if (now.isBefore(fajr)) {
            nextPrayer = "FAJR";
        } else if (now.isBefore(dhuhr)) {
            nextPrayer = "DHUHR";
        } else if (now.isBefore(asr)) {
            nextPrayer = "ASR";
        } else if (now.isBefore(maghrib)) {
            nextPrayer = "MAGHRIB";
        } else if (now.isBefore(isha)) {
            nextPrayer = "ISHA";
        } else {
            // After Isha — next is Imsak of tomorrow
            nextPrayer = "IMSAK";
        }
    }

    public HorairePriere getHoraireAujourdhui() { return horaireAujourdhui; }
    public String getVille() { return ville; }
    public String getNextPrayer() { return nextPrayer; }
    
    public int getQiblaDegree() {
        if (horaireAujourdhui != null) {
            return (int) horaireAujourdhui.getQiblaDegree();
        }
        return 110; // Fallback par défaut si l'API est injoignable
    }
}
