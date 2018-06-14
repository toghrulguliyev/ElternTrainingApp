package at.ac.univie.entertain.elterntrainingapp.model.quizDuel;

public class Results {

    private int playedDuels;
    private int answeredQuestions;
    private int rightAnswered, wrongAnswered;
    private double answeredRight, answeredWrong;

    public Results() {}

    public int getPlayedDuels() {
        return playedDuels;
    }

    public void setPlayedDuels(int playedDuels) {
        this.playedDuels = playedDuels;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(int answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public int getRightAnswered() {
        return rightAnswered;
    }

    public void setRightAnswered(int rightAnswered) {
        this.rightAnswered = rightAnswered;
    }

    public int getWrongAnswered() {
        return wrongAnswered;
    }

    public void setWrongAnswered(int wrongAnswered) {
        this.wrongAnswered = wrongAnswered;
    }

    public double getAnsweredRight() {
        return answeredRight;
    }

    public void setAnsweredRight(double answeredRight) {
        this.answeredRight = answeredRight;
    }

    public double getAnsweredWrong() {
        return answeredWrong;
    }

    public void setAnsweredWrong(double answeredWrong) {
        this.answeredWrong = answeredWrong;
    }

}
