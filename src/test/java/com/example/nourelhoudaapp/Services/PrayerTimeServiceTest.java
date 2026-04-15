package com.example.nourelhoudaapp.Services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrayerTimeServiceTest {

    // ─────────────────────────────────────────────
    // Scénario 3 : Traduction du Calendrier Hijri
    // ─────────────────────────────────────────────

    @Test
    public void testHijriMonthFallback_Ramadan() {
        PrayerTimeService service = new PrayerTimeService();
        String result = service.hijriMonthFallback(9);
        assertEquals("رمضان", result, "Le mois 9 doit retourner 'رمضان' (Ramadan)");
    }

    @Test
    public void testHijriMonthFallback_AllMonthsNotNull() {
        // Test de non-régression : aucun mois valide (1-12) ne doit retourner null ou vide
        PrayerTimeService service = new PrayerTimeService();
        for (int i = 1; i <= 12; i++) {
            String result = service.hijriMonthFallback(i);
            assertNotNull(result, "Le mois " + i + " ne doit jamais retourner null");
            assertFalse(result.isEmpty(), "Le mois " + i + " ne doit jamais retourner une chaîne vide");
        }
    }

    @Test
    public void testHijriMonthFallback_InvalidMonths_BoundaryTest() {
        PrayerTimeService service = new PrayerTimeService();

        // Test aux limites inférieures et supérieures (valeurs impossibles)
        assertEquals("", service.hijriMonthFallback(0),
                "Mois 0 (inexistant) doit retourner chaîne vide sans crash");
        assertEquals("", service.hijriMonthFallback(13),
                "Mois 13 (inexistant) doit retourner chaîne vide sans crash");
        assertEquals("", service.hijriMonthFallback(-1),
                "Mois négatif doit retourner chaîne vide sans crash");
    }

    // ─────────────────────────────────────────────
    // Scénario 4 : Calcul de la Direction Qibla
    // ─────────────────────────────────────────────

    @Test
    public void testCalculateQibla_Alger() {
        // Alger : Lat 36.7538, Lon 3.0588
        // Résultat attendu : la Qibla depuis Alger pointe vers ~100-112° (Est-Sud-Est vers La Mecque)
        double qibla = PrayerTimeService.calculateQibla(36.7538, 3.0588);
        assertTrue(qibla >= 100 && qibla <= 115,
                "La Qibla depuis Alger doit être entre 100° et 115°, obtenu : " + qibla + "°");
    }

    @Test
    public void testCalculateQibla_Paris() {
        // Paris : Lat 48.8566, Lon 2.3522
        // Résultat attendu : ~119° depuis Paris (direction sud-est vers La Mecque)
        double qibla = PrayerTimeService.calculateQibla(48.8566, 2.3522);
        assertTrue(qibla >= 110 && qibla <= 130,
                "La Qibla depuis Paris doit être entre 110° et 130°, obtenu : " + qibla + "°");
    }

    @Test
    public void testCalculateQibla_ResultAlwaysBetween0And360() {
        // Test de non-régression : quelle que soit la ville, l'angle doit toujours être [0, 360[
        double[][] villes = {
            {36.7538,  3.0588},   // Alger
            {48.8566,  2.3522},   // Paris
            {40.7128, -74.0060},  // New York
            {35.6762, 139.6503},  // Tokyo
            {-33.8688, 151.2093} // Sydney
        };
        for (double[] ville : villes) {
            double qibla = PrayerTimeService.calculateQibla(ville[0], ville[1]);
            assertTrue(qibla >= 0 && qibla < 360,
                    "L'angle Qibla doit toujours être entre 0 et 360°, obtenu : " + qibla + "° pour (" + ville[0] + ", " + ville[1] + ")");
        }
    }
}
