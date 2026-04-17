package com.example.nourelhoudaapp.DAO;

import com.example.nourelhoudaapp.HibernateUtil;
import com.example.nourelhoudaapp.entites.Utilisateur;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("Connexion réussie - Simulation Hibernate")
    void testConnexion_Success() {
        try (MockedStatic<HibernateUtil> mockedHibernate = mockStatic(HibernateUtil.class)) {
            // 1. Préparer les mocks
            SessionFactory mockFactory = mock(SessionFactory.class);
            Session mockSession = mock(Session.class);
            Transaction mockTx = mock(Transaction.class);
            Query<Utilisateur> mockQuery = mock(Query.class);

            // 2. Définir le comportement de la chaîne Hibernate
            mockedHibernate.when(HibernateUtil::getSessionFactory).thenReturn(mockFactory);
            when(mockFactory.getCurrentSession()).thenReturn(mockSession);
            when(mockSession.beginTransaction()).thenReturn(mockTx);
            when(mockSession.createQuery(anyString(), eq(Utilisateur.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);

            // 3. Simuler un utilisateur trouvé avec un mot de passe haché
            Utilisateur mockUser = new Utilisateur();
            mockUser.setEmail("test@demo.com");
            mockUser.setPassword(AuthDAO.hachagePassword("secret"));
            when(mockQuery.uniqueResult()).thenReturn(mockUser);

            // 4. Exécuter
            AuthDAO authDAO = new AuthDAO();
            Utilisateur result = authDAO.Connexion("test@demo.com", "secret");

            // 5. Vérifier
            assertNotNull(result);
            assertEquals("test@demo.com", result.getEmail());
            verify(mockTx).commit();
        }
    }
}
