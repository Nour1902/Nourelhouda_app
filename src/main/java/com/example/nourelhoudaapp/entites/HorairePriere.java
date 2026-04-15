package com.example.nourelhoudaapp.entites;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "horaires_prieres",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ville", "date"})
)
public class HorairePriere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false)
    private LocalDate date;



    @Column(nullable = false)
    private LocalTime fajr;

    @Column(nullable = false)
    private LocalTime dhuhr;

    @Column(nullable = false)
    private LocalTime asr;

    @Column(nullable = false)
    private LocalTime maghrib; // Iftar

    @Column(nullable = false)
    private LocalTime isha;

    // Constructeurs
    public HorairePriere() {}

    public HorairePriere(String ville, LocalDate date,  LocalTime fajr,
                         LocalTime dhuhr, LocalTime asr, LocalTime maghrib, LocalTime isha) {
        this.ville   = ville;
        this.date    = date;

        this.fajr    = fajr;
        this.dhuhr   = dhuhr;
        this.asr     = asr;
        this.maghrib = maghrib;
        this.isha    = isha;
    }

    // Getters & Setters
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getVille()                     { return ville; }
    public void setVille(String ville)           { this.ville = ville; }

    public LocalDate getDate()                   { return date; }
    public void setDate(LocalDate date)          { this.date = date; }


    public LocalTime getFajr()                   { return fajr; }
    public void setFajr(LocalTime fajr)          { this.fajr = fajr; }

    public LocalTime getDhuhr()                  { return dhuhr; }
    public void setDhuhr(LocalTime dhuhr)        { this.dhuhr = dhuhr; }

    public LocalTime getAsr()                    { return asr; }
    public void setAsr(LocalTime asr)            { this.asr = asr; }

    public LocalTime getMaghrib()                { return maghrib; }
    public void setMaghrib(LocalTime maghrib)    { this.maghrib = maghrib; }

    public LocalTime getIsha()                   { return isha; }
    public void setIsha(LocalTime isha)          { this.isha = isha; }

    @Transient
    public LocalTime getImsak() {
        return (fajr != null) ? fajr.minusMinutes(15) : null;
    }

    @Transient
    private double qiblaDegree = 110.0;

    public double getQiblaDegree() { return qiblaDegree; }
    public void setQiblaDegree(double qiblaDegree) { this.qiblaDegree = qiblaDegree; }

    @Override
    public String toString() {
        return "HorairePriere{ville='" + ville + "', date=" + date + ", maghrib(Iftar)=" + maghrib + "}";
    }
}