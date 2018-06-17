package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.quizDuel.Duel;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class QuestionActivity extends AppCompatActivity {

    private static final String ARG_DUEL = "duel";
    private Duel duel;
    private View questionView;
    private TextView question, optionA, optionB, optionC, optionD;
    private Button nextBtn, backBtn;
    private ProgressBar progressBar;
    private int count;
    private SharedPreferences sharedPreferences;
    private String type;
    private MyCountDownTimer countDownTimer = new MyCountDownTimer(2*60000, 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Bundle args = this.getIntent().getExtras();
        if (args != null) {
            duel = args.getParcelable(ARG_DUEL);
            if (duel.getAutor().equals(getUsername())) {
                type = "autor";
            }
            if (duel.getOpponent().equals(getUsername())) {
                type = "opponent";
            }
            count = 0;
        } else {
            finish();
            Intent intent;
            intent = new Intent(getApplicationContext(), QuizDuelActivity.class);
            startActivity(intent);
        }

        question = (TextView) findViewById(R.id.qd_question);
        optionA = (TextView) findViewById(R.id.qd_question_a);
        optionB = (TextView) findViewById(R.id.qd_question_b);
        optionC = (TextView) findViewById(R.id.qd_question_c);
        optionD = (TextView) findViewById(R.id.qd_question_d);
        nextBtn = (Button) findViewById(R.id.qd_next_question_btn);
        backBtn = (Button) findViewById(R.id.qd_cancel);
        progressBar = (ProgressBar) findViewById(R.id.qd_progressbar);

        //progressBar.setMax(119);
        //progressBar.setProgress(119);
        //MyCountDownTimer countDownTimer = new MyCountDownTimer(2*60000, 1000);
        //countDownTimer.start();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                finish();
                Intent intent = new Intent(getApplicationContext(), QuizDuelActivity.class);
                startActivity(intent);

            }
        });

        loadQuestion();

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("A Selected");
                optionA.setSelected(false);
                optionB.setSelected(false);
                optionB.setSelected(false);
                optionD.setSelected(false);
                //if (count < duel.getQuestions().size() - 1) {
                    if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionA.getText().toString())) {
                        if (type.equals("autor")) {
                            duel.getScore().plusAutorScore(1);
                        } else if (type.equals("opponent")) {
                            duel.getScore().plusOpponentScore(1);
                        }
                        optionA.setBackgroundColor(Color.GREEN);
                    } else {
                        optionA.setBackgroundColor(Color.RED);
                        if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionB.getText().toString())) {
                            optionB.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionC.getText().toString())) {
                            optionC.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionD.getText().toString())) {
                            optionD.setBackgroundColor(Color.GREEN);
                        }
                    }
                //}
                //count++;
                //loadQuestion();
            }
        });
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("B Selected");
                optionA.setSelected(false);
                optionB.setSelected(false);
                optionB.setSelected(false);
                optionD.setSelected(false);
                //if (count < duel.getQuestions().size() - 1) {
                    if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionB.getText().toString())) {
                        if (type.equals("autor")) {
                            duel.getScore().plusAutorScore(1);
                        } else if (type.equals("opponent")) {
                            duel.getScore().plusOpponentScore(1);
                        }
                        optionB.setBackgroundColor(Color.GREEN);
                    } else {
                        optionB.setBackgroundColor(Color.RED);
                        if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionA.getText().toString())) {
                            optionA.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionC.getText().toString())) {
                            optionC.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionD.getText().toString())) {
                            optionD.setBackgroundColor(Color.GREEN);
                        }
                    }
                //}
                //count++;
                //loadQuestion();
            }
        });
        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("C Selected");
                optionA.setSelected(false);
                optionB.setSelected(false);
                optionB.setSelected(false);
                optionD.setSelected(false);
                //if (count < duel.getQuestions().size() - 1) {
                    if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionC.getText().toString())) {
                        System.out.println("C is the Correct Answer!");
                        if (type.equals("autor")) {
                            duel.getScore().plusAutorScore(1);
                        } else if (type.equals("opponent")) {
                            duel.getScore().plusOpponentScore(1);
                        }
                        optionC.setBackgroundColor(Color.GREEN);
                    } else {
                        optionC.setBackgroundColor(Color.RED);
                        if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionA.getText().toString())) {
                            optionA.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionB.getText().toString())) {
                            optionB.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionD.getText().toString())) {
                            optionD.setBackgroundColor(Color.GREEN);
                        }
                    }
                //}
                //count++;
                //loadQuestion();
            }
        });
        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("D Selected");
                optionA.setSelected(false);
                optionB.setSelected(false);
                optionB.setSelected(false);
                optionD.setSelected(false);
                //if (count < duel.getQuestions().size() - 1) {
                    if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionD.getText().toString())) {
                        if (type.equals("autor")) {
                            duel.getScore().plusAutorScore(1);
                        } else if (type.equals("opponent")) {
                            duel.getScore().plusOpponentScore(1);
                        }
                        optionD.setBackgroundColor(Color.GREEN);
                    } else {
                        optionD.setBackgroundColor(Color.RED);
                        if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionA.getText().toString())) {
                            optionA.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionC.getText().toString())) {
                            optionC.setBackgroundColor(Color.GREEN);
                        } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionB.getText().toString())) {
                            optionB.setBackgroundColor(Color.GREEN);
                        }
                    }
                //}
                //count++;
                //loadQuestion();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("count = " + count + " // duel size = " + duel.getQuestions().size());
                if (count < duel.getQuestions().size() - 1) {
                    count++;
                    optionA.setSelected(false);
                    optionB.setSelected(false);
                    optionB.setSelected(false);
                    optionD.setSelected(false);
                    optionA.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    optionB.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    optionC.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    optionD.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    if (type.equals("autor")) {
                        //System.out.println("Autor Score = " + duel.getAutorScore());
                    } else if (type.equals("opponent")) {
                        //System.out.println("Opponent Score = " + duel.getOpponentScore());
                    }
                    loadQuestion();
                } else {
                    System.out.println("count = " + count);
                    if (type.equals("autor")) {
                        duel.setAutorStatus(true);
                    } else if (type.equals("opponent")) {
                        duel.setOpponentStatus(true);
                    }
                    //setWinner();
                    if (duel.getWinner() == null || duel.getWinner().isEmpty()) {
                        if (duel.isOpponentStatus() && duel.isAutorStatus()) {
                            if (duel.getAutorScore() > duel.getOpponentScore()) {
                                duel.setWinner(duel.getAutor());
                            } else if (duel.getAutorScore() < duel.getOpponentScore()) {
                                duel.setWinner(duel.getOpponent());
                            } else if (duel.getAutorScore() == duel.getOpponentScore()) {
                                duel.setWinner("Unentschieden");
                            }
                        }
                    }
                    countDownTimer.cancel();
                    saveScore();
                    //showResultDialog();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        super.onBackPressed();
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (2*60000 - millisUntilFinished) / 1000;
            progressBar.setProgress(progress);
            System.out.println("progress = " + progress);
        }

        @Override
        public void onFinish() {

            if (count < duel.getQuestions().size() - 1) {
                count++;
                optionA.setSelected(false);
                optionB.setSelected(false);
                optionB.setSelected(false);
                optionD.setSelected(false);
                optionA.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                optionB.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                optionC.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                optionD.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                if (type.equals("autor")) {
                    System.out.println("Autor Score = " + duel.getAutorScore());
                } else if (type.equals("opponent")) {
                    System.out.println("Opponent Score = " + duel.getOpponentScore());
                }
                progressBar.setProgress(119);
                countDownTimer.cancel();
                loadQuestion();
            } else {
                progressBar.setProgress(119);
                countDownTimer.cancel();
                if (type.equals("autor")) {
                    duel.setAutorStatus(true);
                } else if (type.equals("opponent")) {
                    duel.setOpponentStatus(true);
                }
                setWinner();
                saveScore();
                //showResultDialog();
            }
            //Toast.makeText(getActivity(), "5 Minunten sind vorbei", Toast.LENGTH_SHORT).show();
        }

    }

    public void setWinner() {
        if (duel.getWinner() == null || duel.getWinner().isEmpty()) {
            if (duel.isOpponentStatus() && duel.isAutorStatus()) {
                if (duel.getAutorScore() > duel.getOpponentScore()) {
                    duel.setWinner(duel.getAutor());
                } else if (duel.getAutorScore() < duel.getOpponentScore()) {
                    duel.setWinner(duel.getOpponent());
                } else if (duel.getAutorScore() == duel.getOpponentScore()) {
                    duel.setWinner("Unentschieden");
                }
            }
        }
    }

    public void showResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogQuizResult = this.getLayoutInflater().inflate(R.layout.dialog_qd_duel_result, null);
        builder.setView(dialogQuizResult);
        builder.setTitle("Duellergebnis");

        TextView winner = (TextView) dialogQuizResult.findViewById(R.id.qd_display_winner);
        TextView autor_username = (TextView) dialogQuizResult.findViewById(R.id.qd_result_autor_username);
        TextView oppo_username = (TextView) dialogQuizResult.findViewById(R.id.qd_result_gegner_username);
        TextView autor_score = (TextView) dialogQuizResult.findViewById(R.id.qd_result_autor_score);
        TextView oppo_score = (TextView) dialogQuizResult.findViewById(R.id.qd_result_gegner_score);

        if (type.equals("autor")) {
            if (duel.isOpponentStatus()) {
                if (duel.getWinner() == null || duel.getWinner().isEmpty()) {
                    winner.setText("Der Gegner hat noch nicht gespielt");
                    winner.setTextColor(Color.RED);
                } else if (duel.getWinner() != null && !duel.getWinner().isEmpty()) {
                    winner.setText(duel.getWinner());
                    winner.setTextColor(Color.GREEN);
                }
            } else {
                winner.setText("Der Gegner hat noch nicht gespielt");
                winner.setTextColor(Color.RED);
            }
        } else if (type.equals("opponent")) {
            if (duel.isAutorStatus()) {
                if (duel.getWinner() == null || duel.getWinner().isEmpty()) {
                    winner.setText("Der Gegner hat noch nicht gespielt");
                    winner.setTextColor(Color.RED);
                } else if (duel.getWinner() != null && !duel.getWinner().isEmpty()) {
                    winner.setText(duel.getWinner());
                    winner.setTextColor(Color.GREEN);
                }
            } else {
                winner.setText("Der Gegner hat noch nicht gespielt");
                winner.setTextColor(Color.RED);
            }
        }

        autor_username.setText(duel.getAutor());
        oppo_username.setText(duel.getOpponent());
        if (!duel.isOpponentStatus()) {
            oppo_score.setText("Warten auf Punkte");
        } else if (duel.isOpponentStatus()) {
            oppo_score.setText(String.valueOf(duel.getOpponentScore()));
        }
        if (!duel.isAutorStatus()) {
            autor_score.setText("Warten auf Punkte");
        } else if (duel.isAutorStatus()) {
            autor_score.setText(String.valueOf(duel.getAutorScore()));
        }

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent;
                        intent = new Intent(getApplicationContext(), QuizDuelActivity.class);
                        startActivity(intent);
                    }
                });
        builder.show();

    }

    public void saveScore() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String username = getUsername();
        String token = getToken();
        int score = 0;
        if (type.equals("autor")) {
            score = duel.getAutorScore();
        } else if (type.equals("opponent")) {
            score = duel.getOpponentScore();
        }

        showResultDialog();

        Call<Response> call = api.saveScore(token, username, type, duel.getId(), score);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(QuestionActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(QuestionActivity.this, "Verbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadQuestion() {
        System.out.println("Question Count = " + count);
        System.out.println("Question: " + duel.getQuestions().get(count).getQuestion());
        if (duel.getQuestions() != null && !duel.getQuestions().isEmpty()) {
            if (count <= duel.getQuestions().size() - 1) {
                question.setText(duel.getQuestions().get(count).getQuestion());
                List<String> answers = new ArrayList<String>();
                answers = duel.getQuestions().get(count).getShuffeledAnswers();
                optionA.setText(answers.get(0));
                optionB.setText(answers.get(1));
                optionC.setText(answers.get(2));
                optionD.setText(answers.get(3));
                progressBar.setMax(119);
                progressBar.setProgress(119);
                countDownTimer.start();
                return;
            }
        }
    }

    public String getToken() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

}
