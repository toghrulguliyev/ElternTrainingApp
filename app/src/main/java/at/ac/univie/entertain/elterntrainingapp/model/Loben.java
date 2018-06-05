package at.ac.univie.entertain.elterntrainingapp.model;

import java.util.UUID;

public class Loben {

    private String situation, art, reaktion;
    private String autor;
    private String id;

    public Loben() {}

    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getReaktion() {
        return reaktion;
    }

    public void setReaktion(String reaktion) {
        this.reaktion = reaktion;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
