package at.ac.univie.entertain.elterntrainingapp.model;

import java.util.ArrayList;
import java.util.List;

public class MyGoals {

    private List<String> goals;
    private String autor;

    public MyGoals(List<String> goals, String autor) {
        this.goals = new ArrayList<String>();
        this.goals = goals;
        this.autor = autor;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
