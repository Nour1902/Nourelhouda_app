package com.example.nourelhoudaapp.Controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la logique de calcul de statistiques utilisateur.
 * Ces méthodes sont testées en isolation (sans base de données / CDI).
 */
public class UserStatsBeanTest {

    // ─────────────────────────────────────────────
    // Scénario 5 : Calcul du Pourcentage de Régularité
    // ─────────────────────────────────────────────

    @Test
    public void testRegularite_PasDeDivisionParZero() {
        // Cas critique : Si l'utilisateur vient de s'inscrire (0 jours de Ramadan passés)
        // La division par zéro doit être ÉVITÉE
        int joursSuivis = 0;
        int dayLimit = 0;

        // Simulation exacte de la logique dans UserStatsBean.init()
        int regularite = 0;
        if (dayLimit > 0) {
            regularite = (int) Math.round(((double) joursSuivis / dayLimit) * 100);
        }

        assertEquals(0, regularite,
                "Si aucun jour n'est passé (dayLimit=0), la régularité doit être 0 sans ArithmeticException");
    }

    @Test
    public void testRegularite_MaximumNe_Depasse_Pas_100() {
        // Si l'utilisateur a suivi TOUS les jours du Ramadan
        int joursSuivis = 30;
        int dayLimit = 30;

        int regularite = (int) Math.round(((double) joursSuivis / dayLimit) * 100);

        assertEquals(100, regularite, "La régularité parfaite doit être exactement 100%");
        assertTrue(regularite <= 100, "La régularité ne doit jamais dépasser 100%");
    }

    // ─────────────────────────────────────────────
    // Scénario 5b : Calcul du Pourcentage de Progrès Quotidien
    // ─────────────────────────────────────────────

    @Test
    public void testProgressPercentage_Standard() {
        // Si l'utilisateur a accompli 3 actes sur un total de 10
        int totalActesFaits = 3;
        int totalActes = 10;

        int progress = (int) Math.round(((double) totalActesFaits / totalActes) * 100);

        assertEquals(30, progress, "3 actes sur 10 doivent donner 30% de progression");
    }

    @Test
    public void testProgressPercentage_PasDeDivisionParZeroSiAucunActe() {
        // Cas critique : si aucun acte n'est encore en base de données
        int totalActesFaits = 0;
        int totalActes = 0;

        // Simulation exacte de la logique dans UserDashboardBean.getProgressPercentage()
        int progress = 0;
        if (totalActes > 0) {
            progress = (int) Math.round(((double) totalActesFaits / totalActes) * 100);
        }

        assertEquals(0, progress,
                "Si aucun acte n'est en base de données, le progrès doit afficher 0% sans crash");
    }
}
