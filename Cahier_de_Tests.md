# Cahier de Recette et Scénarios de Tests — Nour El Houda

**Projet :** Application de Suivi Spirituel du Ramadan "Nour El Houda"  
**Technologie :** Jakarta EE, Hibernate, WildFly 30, PostgreSQL, JUnit 5  
**Méthodologie de test :** Tests Unitaires Purs (sans base de données) — conformes aux bonnes pratiques de non-régression.

---

## 1. Identification des Fonctionnalités à Tester

À partir du cahier des charges, les fonctionnalités ont été classées par criticité pour garantir la robustesse du projet sans intégrer de dépendances lourdes (base de données, serveur Jakarta EE) dans la suite de tests.

| Priorité | Fonctionnalité | Classe de test |
|---|---|---|
| 🔴 HAUTE | Sécurité : Hachage et vérification des mots de passe | `AuthDAOTest` |
| 🔴 HAUTE | Calcul de la Direction de la Qibla (formule sphérique) | `PrayerTimeServiceTest` |
| 🟠 MOYENNE | Calcul de l'heure d'Imsak à partir du Fajr | `HorairePriereTest` |
| 🟠 MOYENNE | Traduction Numéro → Nom de Mois Hijri en Arabe | `PrayerTimeServiceTest` |
| 🟡 NORMALE | Calcul du % de Régularité et de Progrès sans division par zéro | `UserStatsBeanTest` |

---

## 2. Scénarios de Tests, Cas, Résultats Attendus et Obtenus

---

### Scénario 1 — `AuthDAOTest` : Sécurité du Mot de Passe

**Fonctionnalité testée :** `AuthDAO.hachagePassword()` et `AuthDAO.verfiyPassword()`  
**Objectif :** Garantir que les mots de passe stockés en base de données sont illisibles et résistants.

| ID | Nom du Cas | Entrée | Résultat Attendu | Résultat Obtenu | Statut |
|---|---|---|---|---|---|
| T1.1 | Hachage BCrypt standard | `"admin"` | Hash non nul, différent du clair, commençant par `$2a$` | Conforme | ✅ |
| T1.2 | Vérification mot de passe valide | `"admin"` + hash | `true` | Conforme | ✅ |
| T1.3 | Rejet mot de passe incorrect (casse) | `"Admin"` + hash de `"admin"` | `false` | Conforme | ✅ |

**Anomalie détectée :** Lors du développement, la casse n'était pas strictement contrôlée. Le test T1.3 a formellement prouvé que BCrypt est bien sensible à la casse, ce qui est le comportement attendu.  
**Correction apportée :** Aucune modification nécessaire. BCrypt standard gère correctement ce cas.

---

### Scénario 2 — `HorairePriereTest` : Calcul de l'Heure d'Imsak

**Fonctionnalité testée :** `HorairePriere.getImsak()`  
**Objectif :** Valider la soustraction précise de 15 minutes au Fajr, y compris aux cas limites (minuit, valeur nulle).

| ID | Nom du Cas | Entrée (Fajr) | Résultat Attendu | Résultat Obtenu | Statut |
|---|---|---|---|---|---|
| T2.1 | Calcul standard | `05:00` | `04:45` | Conforme | ✅ |
| T2.2 | Passage à travers minuit | `00:05` | `23:50` | Conforme | ✅ |
| T2.3 | Fajr non chargé (API hors service) | `null` | `null` (pas de crash) | Conforme | ✅ |

**Anomalie détectée :** Une ancienne version du code lançait une `NullPointerException` si l'API AlAdhan tombait en panne et ne fournissait pas d'heure Fajr.  
**Correction apportée :** Ajout d'une protection ternaire dans la méthode `getImsak()` : `return (fajr != null) ? fajr.minusMinutes(15) : null;`

---

### Scénario 3 — `PrayerTimeServiceTest` : Traduction du Calendrier Hijri

**Fonctionnalité testée :** `PrayerTimeService.hijriMonthFallback(int month)`  
**Objectif :** Garantir que la déconnexion du service API n'empêche pas l'affichage du mois islamique courant.

| ID | Nom du Cas | Entrée | Résultat Attendu | Résultat Obtenu | Statut |
|---|---|---|---|---|---|
| T3.1 | Mois Ramadan (9) | `9` | `"رمضان"` | Conforme | ✅ |
| T3.2 | Tous les mois valides (1-12) | `1` à `12` | Aucune chaîne vide ou null | Conforme | ✅ |
| T3.3 | Valeur impossible : 0 | `0` | `""` (chaîne vide, pas de crash) | Conforme | ✅ |
| T3.4 | Valeur impossible : 13 | `13` | `""` (chaîne vide, pas de crash) | Conforme | ✅ |
| T3.5 | Valeur négative : -1 | `-1` | `""` (chaîne vide, pas de crash) | Conforme | ✅ |

**Anomalie détectée :** Les caractères arabes retournés par l'API contenaient des séquences d'échappement Unicode corrompues dans certains environnements (encoding UTF-8 non respecté côté serveur).  
**Correction apportée :** Remplacement de la dépendance à la chaîne JSON de l'API par un tableau de chaînes Java natif définissant localement les 12 noms de mois. Ce pont de repli est désormais testé de manière cyclique.

---

### Scénario 4 — `PrayerTimeServiceTest` : Calcul de la Direction de la Qibla

**Fonctionnalité testée :** `PrayerTimeService.calculateQibla(double lat, double lon)`  
**Objectif :** Valider la formule de trigonométrie sphérique renvoyant l'azimut vers la Kaaba depuis n'importe quelle position mondiale.

| ID | Nom du Cas | Entrée (Lat, Lon) | Résultat Attendu | Résultat Obtenu | Statut |
|---|---|---|---|---|---|
| T4.1 | Depuis Alger | `(36.75, 3.05)` | Entre 100° et 115° | 110° | ✅ |
| T4.2 | Depuis Paris | `(48.85, 2.35)` | Entre 110° et 130° | 119° | ✅ |
| T4.3 | Depuis La Mecque (cas extrême) | `(21.42, 39.82)` | 0° (direction indéfinie) | 0° | ✅ |
| T4.4 | Non-régression : résultat [0°, 360°[ | 5 villes mondiales | Toujours entre 0 et 360 | Conforme | ✅ |

**Anomalie détectée :** La formule brute de atan2() pouvait renvoyer des résultats négatifs (ex: -58°) pour des villes à l'ouest de La Mecque.  
**Correction apportée :** Normalisation systématique via `(qibla + 360) % 360` pour garantir qu'un résultat est toujours dans l'intervalle [0°, 360°[.

---

### Scénario 5 — `UserStatsBeanTest` : Calcul des Statistiques de Tableau de Bord

**Fonctionnalité testée :** Logique de calcul de régularité et de progression dans `UserStatsBean` et `UserDashboardBean`  
**Objectif :** Garantir que les statistiques ne « crashent » jamais avec des divisions par zéro pour un utilisateur qui vient de s'inscrire ou dont la base est vide.

| ID | Nom du Cas | Entrée | Résultat Attendu | Résultat Obtenu | Statut |
|---|---|---|---|---|---|
| T5.1 | Régularité standard | 15 jours suivis / 27 passés | 55% | 55% | ✅ |
| T5.2 | Régularité à l'inscription (dayLimit=0) | 0 suivis / 0 jours passés | 0% (pas d'exception) | 0% | ✅ |
| T5.3 | Régularité parfaite (30/30) | 30 suivis / 30 jours | 100% | 100% | ✅ |
| T5.4 | Progression quotidienne standard | 3 actes / 10 total | 30% | 30% | ✅ |
| T5.5 | Progression si base vide (totalActes=0) | 0 actes / 0 total | 0% (pas d'exception) | 0% | ✅ |

**Anomalie détectée :** Lors du premier déploiement, si la table `actes_adoration` était vide, la page Dashboard crashait avec une `ArithmeticException: / by zero`.  
**Correction apportée :** Ajout d'une condition de garde `if (totalActes > 0)` avant tout calcul de pourcentage dans les méthodes de calcul de progrès.

---

## 3. Architecture des Fichiers de Tests

```
src/test/java/com/example/nourelhoudaapp/
├── Controllers/
│   └── UserStatsBeanTest.java     (Scénario 5 — Statistiques)
├── DAO/
│   └── AuthDAOTest.java           (Scénario 1 — Sécurité)
├── Services/
│   └── PrayerTimeServiceTest.java (Scénarios 3 & 4 — Hijri + Qibla)
└── entites/
    └── HorairePriereTest.java     (Scénario 2 — Horaire Imsak)
```

## 4. Stratégie de Non-Régression

Ces tests sont **isolés** de toute dépendance externe (réseau, base de données, serveur Jakarta EE). Ils peuvent être relancés automatiquement à chaque modification du code via la commande Maven :

```bash
mvn test
```

Le résultat attendu est `BUILD SUCCESS` avec `Tests run: 14, Failures: 0, Errors: 0`.
