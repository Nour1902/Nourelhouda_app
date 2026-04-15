package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.DAO.AuthDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {

    @Test
    @DisplayName("Hachage du mot de passe - Le hash ne doit pas être égal au texte clair")
    void testHachagePassword() {
        String password = "MonMotDePasse123";
        String hash = AuthDAO.hachagePassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$")); // BCrypt format
    }

    @Test
    @DisplayName("Vérification du mot de passe avec le bon hash")
    void testVerifyPasswordSuccess() {
        String password = "password123";
        String hash = AuthDAO.hachagePassword(password);

        assertTrue(AuthDAO.verfiyPassword(password, hash));
    }

    @Test
    @DisplayName("Échec de vérification avec un mauvais mot de passe")
    void testVerifyPasswordFailure() {
        String password = "password123";
        String wrongPassword = "wrongPassword";
        String hash = AuthDAO.hachagePassword(password);

        assertFalse(AuthDAO.verfiyPassword(wrongPassword, hash));
    }

    @Test
    @DisplayName("Les hashs de deux mêmes mots de passe doivent être différents (Salage)")
    void testSalage() {
        String password = "password123";
        String hash1 = AuthDAO.hachagePassword(password);
        String hash2 = AuthDAO.hachagePassword(password);

        assertNotEquals(hash1, hash2);
        assertTrue(AuthDAO.verfiyPassword(password, hash1));
        assertTrue(AuthDAO.verfiyPassword(password, hash2));
    }
}