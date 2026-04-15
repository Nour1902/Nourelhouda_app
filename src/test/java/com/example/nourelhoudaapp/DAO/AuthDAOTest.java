package com.example.nourelhoudaapp.DAO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {

    @Test
    public void testHachagePassword() {
        // Arrange : Préparation du mot de passe
        String plainPassword = "admin";

        // Act : Hachage
        String hash = AuthDAO.hachagePassword(plainPassword);

        // Assert : Vérification
        assertNotNull(hash, "Le hash généré ne doit pas être nul");
        assertNotEquals(plainPassword, hash, "Le hash doit être totalement différent du mot de passe en clair");
        assertTrue(hash.startsWith("$2a$"), "Le hash BCrypt doit commencer par l'identifiant standard 2a");
    }

    @Test
    public void testVerifyPassword_Success() {
        // Arrange
        String plainPassword = "admin";
        String hash = AuthDAO.hachagePassword(plainPassword);

        // Act
        boolean isMatch = AuthDAO.verfiyPassword(plainPassword, hash);

        // Assert
        assertTrue(isMatch, "La vérification doit réussir pour le mot de passe valide");
    }

    @Test
    public void testVerifyPassword_Failure() {
        // Arrange
        String plainPassword = "admin";
        String wrongPassword = "Admin"; // Différence de majuscule !
        String hash = AuthDAO.hachagePassword(plainPassword);

        // Act
        boolean isMatch = AuthDAO.verfiyPassword(wrongPassword, hash);

        // Assert
        assertFalse(isMatch, "La vérification doit absolument échouer pour un mauvais mot de passe ou une mauvaise casse");
    }
}
