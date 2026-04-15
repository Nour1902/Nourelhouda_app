package com.example.nourelhoudaapp.entites;

public enum TypeActePersonnel {
    LECTURE_CORAN("Lecture du Coran"),
    ZAKAT_SADAQAH("Zakat / Sadaqah"),
    DHIKR("Dhikr"),
    NAWAFIL("Prières Nawafil"),
    VISITE_FAMILIALE("Visite Familiale (Silat Al-Rahim)"),
    AIDE_PROCHAIN("Aide au Prochain"),
    ASSISES_SCIENCE("Assise de Science / Conférence"),
    AUTRE("Autre Action");

    private final String label;

    TypeActePersonnel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
