package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.AuthDAO;
import com.example.nourelhoudaapp.entites.Utilisateur;
import com.example.nourelhoudaapp.Utile.Role;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitaire pour AuthBean.Connecter().
 * Utilise Mockito-inline pour mocker l'instanciation manuelle (new AuthDAO()) 
 * et les appels statiques à FacesContext.
 */
public class AuthBeanTest {

    private MockedStatic<FacesContext> mockedFacesContext;

    @BeforeEach
    void setUp() {
        mockedFacesContext = mockStatic(FacesContext.class);
    }

    @AfterEach
    void tearDown() {
        // Fermeture obligatoire du mock statique
        mockedFacesContext.close();
    }

    @Test
    @DisplayName("Succès de connexion - Rôle Admin")
    void testConnecter_Succes_Admin() {
        // Utilisation de MockConstruction pour intercepter "new AuthDAO()" dans le constructeur de AuthBean
        try (MockedConstruction<AuthDAO> mockedDAO = mockConstruction(AuthDAO.class, (mock, context) -> {
            Utilisateur admin = new Utilisateur();
            admin.setEmail("admin@nourelhouda.com");
            admin.setRole(Role.Admin);
            when(mock.Connexion(anyString(), anyString())).thenReturn(admin);
        })) {
            // L'instance de AuthBean créera un mock de AuthDAO via son constructeur
            AuthBean authBean = new AuthBean();
            authBean.getUser().setEmail("admin@nourelhouda.com");
            authBean.getUser().setPassword("admin123");

            String result = authBean.Connecter();

            // Vérifications
            assertEquals("/EspaceAdmin/dashboardAdmin.xhtml?faces-redirect=true", result);
            assertEquals(Role.Admin, authBean.getUser().getRole());
            assertEquals("admin@nourelhouda.com", authBean.getUser().getEmail());
        }
    }

    @Test
    @DisplayName("Succès de connexion - Rôle Utilisateur")
    void testConnecter_Succes_User() {
        try (MockedConstruction<AuthDAO> mockedDAO = mockConstruction(AuthDAO.class, (mock, context) -> {
            Utilisateur user = new Utilisateur();
            user.setEmail("user@test.com");
            user.setRole(Role.User);
            when(mock.Connexion(anyString(), anyString())).thenReturn(user);
        })) {
            AuthBean authBean = new AuthBean();
            authBean.getUser().setEmail("user@test.com");
            authBean.getUser().setPassword("user123");

            String result = authBean.Connecter();


            assertEquals("/EspaceUser/dashboardUser.xhtml?faces-redirect=true", result);
            assertEquals(Role.User, authBean.getUser().getRole());
        }
    }

    @Test
    @DisplayName("Échec de connexion - Identifiants incorrects")
    void testConnecter_Echec() {
        // Simuler le retour de FacesContext pour addMessage()
        FacesContext facesContext = mock(FacesContext.class);
        mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

        try (MockedConstruction<AuthDAO> mockedDAO = mockConstruction(AuthDAO.class, (mock, context) -> {
            when(mock.Connexion(anyString(), anyString())).thenReturn(null);
        })) {
            AuthBean authBean = new AuthBean();
            authBean.getUser().setEmail("inconnu@test.com");
            authBean.getUser().setPassword("wrongpass");

            String result = authBean.Connecter();

            // Vérifications
            assertNull(result);
            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
        }
    }
}
