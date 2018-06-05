package at.ac.univie.entertain.elterntrainingapp.model;

import java.util.UUID;

public class Gedanke {

    private String situation, bewertung, feel, altBewertung, altReaktion;
    private String autor;
    private String id;

    public Gedanke() {
        this.id = UUID.randomUUID().toString();
    }

    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getBewertung() {
        return bewertung;
    }

    public void setBewertung(String bewertung) {
        this.bewertung = bewertung;
    }

    public String getFeel() {
        return feel;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getAltBewertung() {
        return altBewertung;
    }

    public void setAltBewertung(String altBewertung) {
        this.altBewertung = altBewertung;
    }

    public String getAltReaktion() {
        return altReaktion;
    }

    public void setAltReaktion(String altReaktion) {
        this.altReaktion = altReaktion;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
