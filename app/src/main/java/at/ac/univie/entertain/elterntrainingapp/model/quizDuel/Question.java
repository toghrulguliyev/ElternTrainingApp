package at.ac.univie.entertain.elterntrainingapp.model.quizDuel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question implements Parcelable {

    private String question;
    private String category;
    private List<String> answers = new ArrayList<String>();
    private String correctAnswer;

    public Question(String quest, List<String> answers, String correctAns, String category) {
        this.question = quest;
        this.answers = answers;
        this.correctAnswer = correctAns;
        this.category = category;
    }

    public List<String> getShuffeledAnswers() {
        Collections.shuffle(answers);
        return answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void addAnswer(String answer) {
        this.answers.add(answer);
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.question);
        dest.writeString(this.category);
        dest.writeStringList(this.answers);
        dest.writeString(this.correctAnswer);
    }

    protected Question(Parcel in) {
        this.question = in.readString();
        this.category = in.readString();
        this.answers = in.createStringArrayList();
        this.correctAnswer = in.readString();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
