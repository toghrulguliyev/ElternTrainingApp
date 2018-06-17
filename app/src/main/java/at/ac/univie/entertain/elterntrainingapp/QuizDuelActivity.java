package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.DuelAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import at.ac.univie.entertain.elterntrainingapp.model.quizDuel.Duel;
import at.ac.univie.entertain.elterntrainingapp.model.quizDuel.Results;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class QuizDuelActivity extends AppCompatActivity {


    private static final String ARG_DUEL = "duel";

    private ListView duelListView;
    private List<Duel> duelList;
    private DuelAdapter duelAdapter;
    private Button resultsBtn, backBtn;
    private SharedPreferences sharedPreferences;
    private String[] cat = {"Erziehungskompetenz", "Konfliktösung", "Allgemeinwissen", "Umgang miteinander", "Alltagssituationen"};
    private List<User> membersList = new ArrayList<User>();
    private List<String> allMembers;
    private String category, opponent;
    private Results results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_duel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                createGame();
            }
        });

        duelListView = (ListView) findViewById(R.id.qd_duel_list);
        resultsBtn = (Button) findViewById(R.id.qd_ergebnisse_btn);
        backBtn = (Button) findViewById(R.id.qd_back_btn);

        loadMemberList();
        loadDuelList();


        resultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadResults();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void showOverallResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogResults = this.getLayoutInflater().inflate(R.layout.dialog_qd_results, null);
        builder.setView(dialogResults);
        builder.setTitle("Ergebnisse");

        TextView playedDuels = (TextView) dialogResults.findViewById(R.id.qd_played_duels);
        TextView answeredQuestions = (TextView) dialogResults.findViewById(R.id.qd_answered_questions);
        TextView rightAnswers = (TextView) dialogResults.findViewById(R.id.qd_right_answers);
        TextView falseAnswers = (TextView) dialogResults.findViewById(R.id.qd_false_answers);
        TextView rightPercentage = (TextView) dialogResults.findViewById(R.id.qd_right_answers_percent);
        TextView falsePercentage = (TextView) dialogResults.findViewById(R.id.qd_wrong_answers_percent);

        System.out.println("Results = " + String.valueOf(results.getAnsweredWrong()));

        if (results != null) {
            playedDuels.setText(String.valueOf(results.getPlayedDuels()));
            answeredQuestions.setText(String.valueOf(results.getAnsweredQuestions()));
            rightAnswers.setText(String.valueOf(results.getRightAnswered()));
            falseAnswers.setText(String.valueOf(results.getWrongAnswered()));
            rightPercentage.setText(String.valueOf(results.getAnsweredRight()) + " %");
            falsePercentage.setText(String.valueOf(results.getAnsweredWrong()) + " %");
        } else {
            playedDuels.setText("");
            answeredQuestions.setText("");
            rightAnswers.setText("");
            falseAnswers.setText("");
            rightPercentage.setText("");
            falsePercentage.setText("");
        }

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();

    }

    public void loadResults() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();
        Call<Results> call = api.getScores(token, username);

        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, retrofit2.Response<Results> response) {
                if (response.isSuccessful()) {
                    results = new Results();
                    results = response.body();
                    if (results != null) {
                        showOverallResults();
                    } else {
                        new AlertDialog.Builder(getApplicationContext())
                                .setMessage("Keine Ergebnisse vorhanden!")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(QuizDuelActivity.this, "Internetverbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogNewQuiz = this.getLayoutInflater().inflate(R.layout.item_new_duel, null);
        builder.setView(dialogNewQuiz);
        builder.setTitle("Duell erstellen");

        Spinner categories = (Spinner) dialogNewQuiz.findViewById(R.id.qd_fragenkategorien);
        Spinner opponents = (Spinner) dialogNewQuiz.findViewById(R.id.qd_gegner_spinner);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cat);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(categoriesAdapter);

        if (membersList != null && !membersList.isEmpty()) {
            final ArrayAdapter<String> allMembersAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, allMembers);
            allMembersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            opponents.setAdapter(allMembersAdapter);
            opponents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    opponent = allMembers.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = cat[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setCancelable(false)
                .setPositiveButton("Erstellen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((category == null || category.isEmpty() && (opponent == null || opponent.isEmpty()))) {
                            Toast.makeText(QuizDuelActivity.this, "Alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
                        } else if ((category != null && !category.isEmpty() && (opponent != null && !opponent.isEmpty()))) {
                            createDuel(category, opponent);
                        }
                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void createDuel(String category, String opponent) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<Response> call = api.createDuel(token, username, category, opponent);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(QuizDuelActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadDuelList();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(QuizDuelActivity.this, "Keine Internet Verbindung", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAllMembers() {
        this.allMembers = new ArrayList<String>();
        if (this.membersList == null || this.membersList.isEmpty()) {
            return;
        } else if (this.membersList != null && !this.membersList.isEmpty()) {
            for (User user : membersList) {
                allMembers.add(user.getUsername());
            }
        } else {
            return;
        }
    }

    public void loadDuelList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();
        Call<List<Duel>> call = api.getDuels(token, username);

        call.enqueue(new Callback<List<Duel>>() {
            @Override
            public void onResponse(Call<List<Duel>> call, retrofit2.Response<List<Duel>> response) {
                if (response.isSuccessful()) {
                    duelList = response.body();
                    if (duelList == null || duelList.isEmpty()) {
                        Toast.makeText(QuizDuelActivity.this, "Keine Duelle gefunden", Toast.LENGTH_SHORT).show();
                    } else if (duelList != null && !duelList.isEmpty()) {
                        duelAdapter = new DuelAdapter(QuizDuelActivity.this, duelList);
                        System.out.println("duelAdapter Class: " + duelAdapter.getCount());
                        duelListView.setAdapter(duelAdapter);
                        System.out.println("duelListView Count: " + duelListView.toString());
                        duelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (duelList.get(position).getAutor().equals(getUsername())) {
                                    if (duelList.get(position).isAutorStatus()) {
                                        showResultDialog(position);
                                    } else if (!duelList.get(position).isAutorStatus()) {
                                        startGame(position);
                                    }
                                } else if (duelList.get(position).getOpponent().equals(getUsername())) {
                                    if (duelList.get(position).isOpponentStatus()) {
                                        showResultDialog(position);
                                    } else if (!duelList.get(position).isOpponentStatus()) {
                                        startGame(position);
                                    }
                                }

                            }
                        });
                        duelListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                removeDueldialog(duelList.get(position).getId());
                                return true;
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Duel>> call, Throwable t) {
                Toast.makeText(QuizDuelActivity.this, "Keine Internet Verbindung", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeDueldialog(String id) {

        final String mId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wirklich löschen?");

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDuel(mId);

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void removeDuel(String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.removeDuel(token, id);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(QuizDuelActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadDuelList();
                } else {
                    Toast.makeText(QuizDuelActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(QuizDuelActivity.this, "Internetverbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showResultDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogQuizResult = this.getLayoutInflater().inflate(R.layout.dialog_qd_duel_result, null);
        builder.setView(dialogQuizResult);
        builder.setTitle("Duellergebnis");

        TextView winner = (TextView) dialogQuizResult.findViewById(R.id.qd_display_winner);
        TextView autor_username = (TextView) dialogQuizResult.findViewById(R.id.qd_result_autor_username);
        TextView oppo_username = (TextView) dialogQuizResult.findViewById(R.id.qd_result_gegner_username);
        TextView autor_score = (TextView) dialogQuizResult.findViewById(R.id.qd_result_autor_score);
        TextView oppo_score = (TextView) dialogQuizResult.findViewById(R.id.qd_result_gegner_score);

        if (!duelList.get(position).isOpponentStatus()) {
            winner.setText("Der Gegner hat noch nicht gespielt");
            winner.setTextColor(Color.MAGENTA);
        } else {
            if (duelList.get(position).getWinner() != null && !duelList.get(position).getWinner().isEmpty()) {
                winner.setText(duelList.get(position).getWinner());
                winner.setTextColor(Color.GREEN);
            } else if (duelList.get(position).getWinner() == null || duelList.get(position).getWinner().isEmpty()) {
                winner.setText("Der Gegner hat noch nicht gespielt");
                winner.setTextColor(Color.MAGENTA);
            }
        }

        autor_username.setText(duelList.get(position).getAutor());
        oppo_username.setText(duelList.get(position).getOpponent());
        if (!duelList.get(position).isOpponentStatus()) {
            oppo_score.setText("Warten auf Punkte");
        } else if (duelList.get(position).isOpponentStatus()) {
            oppo_score.setText(String.valueOf(duelList.get(position).getOpponentScore()) + "/10");
        }
        if (!duelList.get(position).isAutorStatus()) {
            autor_score.setText("Warten auf Punkte");
        } else if (duelList.get(position).isAutorStatus()) {
            autor_score.setText(String.valueOf(duelList.get(position).getAutorScore()) + "/10");
        }

        builder.setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

    }

    public void startGame(int position) {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogQuizStart = this.getLayoutInflater().inflate(R.layout.dialog_qd_duel_result, null);
        builder.setView(dialogQuizStart);
        builder.setTitle("Duellstatus");

        TextView winner = (TextView) dialogQuizStart.findViewById(R.id.qd_display_winner);
        TextView autor_username = (TextView) dialogQuizStart.findViewById(R.id.qd_result_autor_username);
        TextView oppo_username = (TextView) dialogQuizStart.findViewById(R.id.qd_result_gegner_username);
        TextView autor_score = (TextView) dialogQuizStart.findViewById(R.id.qd_result_autor_score);
        TextView oppo_score = (TextView) dialogQuizStart.findViewById(R.id.qd_result_gegner_score);

        winner.setText("Du hast noch nicht gespielt!");
        winner.setTextColor(Color.RED);
        autor_username.setText(duelList.get(position).getAutor());
        oppo_username.setText(duelList.get(position).getOpponent());
        if (!duelList.get(position).isOpponentStatus()) {
            oppo_score.setText("Warten auf Punkte");
        } else if (duelList.get(position).isOpponentStatus()) {
            oppo_score.setText(String.valueOf(duelList.get(position).getOpponentScore()));
        }
        if (!duelList.get(position).isAutorStatus()) {
            autor_score.setText("Warten auf Punkte");
        } else if (duelList.get(position).isAutorStatus()) {
            autor_score.setText(String.valueOf(duelList.get(position).getAutorScore()));
        }

        builder.setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //openGameFragment(duelList.get(pos));
                        openGame(duelList.get(pos));
                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void openGameFragment(Duel duel) {
        Fragment gameFragment = new QuestionFragment();
        Bundle args = new Bundle();
        if (duel != null) {
            args.putParcelable(ARG_DUEL, duel);
            gameFragment.setArguments(args);
        }
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_quiz_duel, gameFragment).addToBackStack(null).commit();
    }

    public void openGame(Duel duel) {
        Intent intent = new Intent(this, QuestionActivity.class);
        Bundle args = new Bundle();
        if (duel != null) {
            args.putParcelable(ARG_DUEL, duel);
            intent.putExtras(args);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Achtung")
                    .setMessage("Duel nicht gefunden! Probieren Sie es später nochmal.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    public void loadMemberList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<List<User>> call = api.getMembers(token, username);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, retrofit2.Response<List<User>> response) {
                if (response.isSuccessful()) {
                    membersList = new ArrayList<User>();
                    membersList = response.body();
                    if (membersList == null || membersList.isEmpty()) {

                    } else if (membersList != null && !membersList.isEmpty() && membersList.size() > 0) {
                        setAllMembers();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Fehler auf dem Server");
            }
        });

    }

    public String getToken() {
        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

    public String getFamilyId() {
        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.FAMILY_ID,"");
    }

}
