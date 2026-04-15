package com.example.nourelhoudaapp.entites;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

public class HorairePriereTest {

    @Test
    public void testGetImsak_StandardTime() {
        // Scénario : Vérification du calcul du Imsak (Fixé à -15 minutes du Fajr)
        
        HorairePriere horaire = new HorairePriere("Paris", LocalDate.now(), 
            LocalTime.of(5, 0), // Fajr à 05:00
            LocalTime.of(13, 0), LocalTime.of(17, 0), 
            LocalTime.of(20, 0), LocalTime.of(21, 30));

        LocalTime imsakObtenu = horaire.getImsak();
        LocalTime imsakAttendu = LocalTime.of(4, 45); // 05:00 - 15 mins = 04:45
        
        assertEquals(imsakAttendu, imsakObtenu, "L'imsak doit être exactement 15 minutes avant le Fajr");
    }

    @Test
    public void testGetImsak_MidnightCrossing() {
        // Vérification de la robustesse si le Fajr est à minuit ou presque
        HorairePriere horaire = new HorairePriere();
        horaire.setFajr(LocalTime.of(0, 5)); // Fajr à 00h05

        LocalTime imsakObtenu = horaire.getImsak();
        LocalTime imsakAttendu = LocalTime.of(23, 50); // Doit passer au soir précédent logiquement (23h50)

        assertEquals(imsakAttendu, imsakObtenu, "La soustraction à travers minuit doit ramener l'heure à 23h50");
    }

    @Test
    public void testGetImsak_NullFajr() {
        // Prévention d'un NullPointerException
        HorairePriere horaire = new HorairePriere();
        // Le Fajr n'est volontairement pas set (null)

        LocalTime imsakObtenu = horaire.getImsak();

        assertNull(imsakObtenu, "Si Fajr est null, l'imsak doit simplement renvoyer null sans crasher l'application");
    }
}
