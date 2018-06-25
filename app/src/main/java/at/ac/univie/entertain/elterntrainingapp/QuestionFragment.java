package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Context.MODE_PRIVATE;


public class QuestionFragment extends Fragment {

    private static final String ARG_DUEL = "duel";
    private Duel duel;
    private View questionView;
    private TextView question, optionA, optionB, optionC, optionD;
    private Button nextBtn, backBtn;
    private ProgressBar progressBar;
    private int count;
    private SharedPreferences sharedPreferences;
    private String type;

    public QuestionFragment() {
        // Required empty public constructor
    }


    public static QuestionFragment newInstance(String param1, String param2) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.duel = (Duel) getArguments().getParcelable(ARG_DUEL);
            if (duel.getAutor().equals(getUsername())) {
                type = "autor";
            }
            if (duel.getOpponent().equals(getUsername())) {
                type = "opponent";
            }
        }

        count = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        questionView = inflater.inflate(R.layout.fragment_question, container, false);

        question = (TextView) questionView.findViewById(R.id.qd_question);
        optionA = (TextView) questionView.findViewById(R.id.qd_question_a);
        optionB = (TextView) questionView.findViewById(R.id.qd_question_b);
        optionC = (TextView) questionView.findViewById(R.id.qd_question_c);
        optionD = (TextView) questionView.findViewById(R.id.qd_question_d);
        nextBtn = (Button) questionView.findViewById(R.id.qd_next_question_btn);
        backBtn = (Button) questionView.findViewById(R.id.qd_cancel);

        loadQuestion();

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                //count++;
                //loadQuestion();
            }
        });
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionB.getText().toString())) {
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
                    } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionD.getText().toString())) {
                        optionD.setBackgroundColor(Color.GREEN);
                    }
                }
                //count++;
                //loadQuestion();
            }
        });
        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionC.getText().toString())) {
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
                    } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionB.getText().toString())) {
                        optionB.setBackgroundColor(Color.GREEN);
                    } else if (duel.getQuestions().get(count).getCorrectAnswer().equals(optionD.getText().toString())) {
                        optionD.setBackgroundColor(Color.GREEN);
                    }
                }
                //count++;
                //loadQuestion();
            }
        });
        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                //count++;
                //loadQuestion();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < duel.getQuestions().size()) {
                    count++;
                    loadQuestion();
                } else {
                    if (type.equals("autor")) {
                        duel.setAutorStatus(true);
                    } else if (type.equals("opponent")) {
                        duel.setOpponentStatus(true);
                    }
                    setWinner();
                    saveScore();
                    showResultDialog();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(QuestionFragment.this).commitAllowingStateLoss();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), QuizDuelActivity.class);
                startActivity(intent);
            }
        });

        return questionView;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogQuizResult = getActivity().getLayoutInflater().inflate(R.layout.dialog_qd_duel_result, null);
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
            oppo_score.setText(duel.getOpponentScore());
        }
        if (!duel.isAutorStatus()) {
            oppo_score.setText("Warten auf Punkte");
        } else if (duel.isAutorStatus()) {
            oppo_score.setText(duel.getAutorScore());
        }

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        Intent intent;
                        intent = new Intent(getActivity(), QuizDuelActivity.class);
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
        int score = new Integer(null);
        if (type.equals("autor")) {
            score = duel.getAutorScore();
        } else if (type.equals("opponent")) {
            score = duel.getOpponentScore();
        }

        Call<Response> call = api.saveScore(token, username, type, duel.getId(), score);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Verbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadQuestion() {

            if (duel.getQuestions() != null && !duel.getQuestions().isEmpty()) {
                if (count < duel.getQuestions().size()) {
                    question.setText(duel.getQuestions().get(count).getQuestion());
                    List<String> answers = new ArrayList<String>();
                    answers = duel.getQuestions().get(count).getShuffeledAnswers();
                    optionA.setText(answers.get(0));
                    optionB.setText(answers.get(1));
                    optionC.setText(answers.get(2));
                    optionD.setText(answers.get(3));
                    return;
            }
        }
    }

    public String getToken() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

}
