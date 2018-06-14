package at.ac.univie.entertain.elterntrainingapp.model.quizDuel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {

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
}
