package at.ac.univie.entertain.elterntrainingapp.model.quizDuel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Duel implements Parcelable {

    private String id;
    private String autor;
    private String opponent;
    private List<Question> questions = new ArrayList<Question>();
    private String status;
    private String category;
    private Score score;
    private boolean autorStatus, opponentStatus;
    private String winner;

    public Duel(String autor, String opponent, List<Question> questions, String status, String category) {
        this.autor = autor;
        this.opponent = opponent;
        this.questions = questions;
        this.status = status;
        this.category = category;
        this.id = UUID.randomUUID().toString();
        this.score = new Score(this.id, this.autor, this.opponent, this.category);
        this.autorStatus = false;
        this.opponentStatus = false;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public boolean isAutorStatus() {
        return autorStatus;
    }

    public void setAutorStatus(boolean autorStatus) {
        this.autorStatus = autorStatus;
    }

    public boolean isOpponentStatus() {
        return opponentStatus;
    }

    public void setOpponentStatus(boolean opponentStatus) {
        this.opponentStatus = opponentStatus;
    }

    public List<Question> getShuffeledQuestions() {
        Collections.shuffle(questions);
        return questions;
    }

    public void setAutorScore(int score) {
        this.score.setScoreAutor(score);
    }

    public void setOpponentScore(int score) {
        this.score.setScoreOpponent(score);
    }

    public Score getScore() {
        return this.score;
    }

    public int getAutorScore() {
        return this.score.getScoreAutor();
    }

    public int getOpponentScore() {
        return this.score.getScoreOpponent();
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

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.autor);
        dest.writeString(this.opponent);
        dest.writeList(this.questions);
        dest.writeString(this.status);
        dest.writeString(this.category);
        dest.writeParcelable(this.score, flags);
        dest.writeByte(this.autorStatus ? (byte) 1 : (byte) 0);
        dest.writeByte(this.opponentStatus ? (byte) 1 : (byte) 0);
        dest.writeString(this.winner);
    }

    protected Duel(Parcel in) {
        this.id = in.readString();
        this.autor = in.readString();
        this.opponent = in.readString();
        this.questions = new ArrayList<Question>();
        in.readList(this.questions, Question.class.getClassLoader());
        this.status = in.readString();
        this.category = in.readString();
        this.score = in.readParcelable(Score.class.getClassLoader());
        this.autorStatus = in.readByte() != 0;
        this.opponentStatus = in.readByte() != 0;
        this.winner = in.readString();
    }

    public static final Parcelable.Creator<Duel> CREATOR = new Parcelable.Creator<Duel>() {
        @Override
        public Duel createFromParcel(Parcel source) {
            return new Duel(source);
        }

        @Override
        public Duel[] newArray(int size) {
            return new Duel[size];
        }
    };
}
