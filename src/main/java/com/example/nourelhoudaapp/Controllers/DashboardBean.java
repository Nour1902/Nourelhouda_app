package com.example.nourelhoudaapp.Controllers;

import com.example.nourelhoudaapp.DAO.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("dashboardBean")
@RequestScoped
public class DashboardBean implements Serializable {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ActeadorationDAO acteDAO      = new ActeadorationDAO();
    private final CitationDAO citationDAO       = new CitationDAO();
    private final ConseilDAO conseilDAO         = new ConseilDAO();

    public long getNbUsers()     { return utilisateurDAO.getALLUser().size(); }
    public long getNbActes()     { return acteDAO.getAllACte().size(); }
    public long getNbCitations() { return citationDAO.findAll().size(); }
    public long getNbConseils()  { return conseilDAO.findAll().size(); }

    public long getNbHommes() {
        return utilisateurDAO.getALLUser().stream()
                .filter(u -> "Homme".equals(String.valueOf(u.getGenre())))
                .count();
    }
    public long getNbFemmes() {
        return utilisateurDAO.getALLUser().stream()
                .filter(u -> "Femme".equals(String.valueOf(u.getGenre())))
                .count();
    }
}