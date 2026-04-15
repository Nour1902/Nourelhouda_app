package com.example.nourelhoudaapp.Controllers;
import com.example.nourelhoudaapp.Controllers.AuthBean;
import com.example.nourelhoudaapp.Utile.Role;
import com.example.nourelhoudaapp.entites.Utilisateur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class AuthBeanTest {

    @Test
    @DisplayName("Redirection correct : User simple doit allez vers dashboard User")
    void TestRedurectUserValide(){
        AuthBean authBean= new AuthBean();
        Utilisateur usersimple=new Utilisateur();
        usersimple.setRole(Role.User);
       String result= authBean.determinerRedirection(usersimple);
       assertEquals("/EspaceUser/dashboardUser.xhtml?faces-redirect=true",result);

    }

    @Test
    @DisplayName("Cas de User=Null")
    void TestRedirectionUserEchec(){
        AuthBean authBean=new AuthBean();
        String result = authBean.determinerRedirection(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Redirection valide de Admin ")
    void TestRedurectAdminValide(){
        AuthBean authBean= new AuthBean();
        Utilisateur userAdmin=new Utilisateur();
        userAdmin.setRole(Role.Admin);
        String result= authBean.determinerRedirection(userAdmin);

        assertEquals( "/EspaceAdmin/dashboardAdmin.xhtml?faces-redirect=true",result);
    }
    @Test
    @DisplayName("Cas de Admin=Null")
    void TestRedirectionAdminEchec(){
        AuthBean authBean=new AuthBean();
        String result = authBean.determinerRedirection(null);
        assertNull(result);
    }

}