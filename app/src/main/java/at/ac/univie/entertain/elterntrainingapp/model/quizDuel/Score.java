package at.ac.univie.entertain.elterntrainingapp.model.quizDuel;

import android.os.Parcel;
import android.os.Parcelable;

public class Score implements Parcelable {

    private String id;
    private String autor, opponent;
    private String category;
    private int scoreAutor, scoreOpponent;


    public Score(String id, String autor, String opponent, String category) {
        this.id = id;
        this.autor = autor;
        this.opponent = opponent;
        this.category = category;
    }

    public void plusAutorScore(int score) {
        this.scoreAutor = this.scoreAutor + score;
    }

    public void plusOpponentScore(int score) {
        this.scoreOpponent = this.scoreOpponent + score;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getScoreAutor() {
        return scoreAutor;
    }

    public void setScoreAutor(int scoreAutor) {
        this.scoreAutor = scoreAutor;
    }

    public int getScoreOpponent() {
        return scoreOpponent;
    }

    public void setScoreOpponent(int scoreOpponent) {
        this.scoreOpponent = scoreOpponent;
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
        dest.writeString(this.category);
        dest.writeInt(this.scoreAutor);
        dest.writeInt(this.scoreOpponent);
    }

    protected Score(Parcel in) {
        this.id = in.readString();
        this.autor = in.readString();
        this.opponent = in.readString();
        this.category = in.readString();
        this.scoreAutor = in.readInt();
        this.scoreOpponent = in.readInt();
    }

    public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel source) {
            return new Score(source);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
}
