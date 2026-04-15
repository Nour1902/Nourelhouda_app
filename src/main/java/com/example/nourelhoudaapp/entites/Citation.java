package com.example.nourelhoudaapp.entites;

import com.example.nourelhoudaapp.Utile.Type;
import jakarta.persistence.*;

import java.security.PublicKey;

@Entity
@Table(name="citation")
public class Citation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false , length = 200)
    private String Source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(name="jour_ramadan",nullable = false)
    private Integer JourRamadan;


    public Citation(){}

    public Citation(String text, String source, Type type, Integer jourRamadan) {
        this.text = text;
        Source = source;
        this.type = type;
        JourRamadan = jourRamadan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getJourRamadan() {
        return JourRamadan;
    }

    public void setJourRamadan(Integer jourRamadan) {
        JourRamadan = jourRamadan;
    }
}
