package at.ac.univie.entertain.elterntrainingapp.model;

import java.util.ArrayList;
import java.util.List;

public class Goals {

    private List<String> realGoals = new ArrayList<String>();
    private List<String> unrealGoals = new ArrayList<String>();

    public Goals(List<String> realGoals, List<String> unrealGoals) {
        this.realGoals = realGoals;
        this.unrealGoals = unrealGoals;
    }

    public List<String> getRealGoals() {
        return realGoals;
    }

    public void setRealGoals(List<String> goals) {
        this.realGoals = goals;
    }

    public void addRealGoal(String realGoal) {
        this.realGoals.add(realGoal);
    }

    public void addUnrealGoal(String unrealGoal) {
        this.unrealGoals.add(unrealGoal);
    }

    public List<String> getUnrealGoals() {
        return unrealGoals;
    }

    public void setUnrealGoals(List<String> unrealGoals) {
        this.unrealGoals = unrealGoals;
    }
}
