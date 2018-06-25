package at.ac.univie.entertain.elterntrainingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.ConsequenceAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.Consequence;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.service.PdfService.PdfHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;


public class ConsequenceFragment extends Fragment {

    View consView;
    private List<Consequence> consList;
    private Consequence cons = new Consequence();
    private Consequence newCons;
    private Consequence newLoben;
    private Button updBtn, goBackBtn, addBtn, savePdf;
    private SharedPreferences sharedPreferences;
    private ConsequenceAdapter consAdapter;
    private ListView consListView;
    private EditText situation, konsequenz, reaktion;
    private ProgressBar progressBar;


    public ConsequenceFragment() {
        // Required empty public constructor
    }

    public static ConsequenceFragment newInstance(String param1, String param2) {
        ConsequenceFragment fragment = new ConsequenceFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        consView = inflater.inflate(R.layout.fragment_consequence, container, false);

        addBtn = (Button) consView.findViewById(R.id.cons_addBtn);
        updBtn = (Button) consView.findViewById(R.id.cons_updateBtn);
        goBackBtn = (Button) consView.findViewById(R.id.cons_backBtn);
        consListView = (ListView) consView.findViewById(R.id.consequence_listview);
        savePdf = (Button) consView.findViewById(R.id.cons_saveToPdf_Btn);
        progressBar = (ProgressBar) consView.findViewById(R.id.progressbar_consequence);

        progressBar.setMax(299);
        progressBar.setProgress(299);
        final MyCountDownTimer countDownTimer = new MyCountDownTimer(5*60000, 1000);
        countDownTimer.start();

        loadConsList();

        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStoragePermissionGranted()) {
                    if (!consList.isEmpty()) {
                        PdfHandler pdf = new PdfHandler(getToken(), getUsername(), getActivity());
                        try {
                            pdf.createCons(consList, "Übung zum Einfluss dysfunktionaler Gedanken auf die Erziehung");
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(), "PDF Datei gespeichert", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Give Permission and try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConsequenseDialog();
            }
        });

        updBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadConsList();
            }
        });

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                getFragmentManager().beginTransaction().remove(ConsequenceFragment.this).commitAllowingStateLoss();
                getFragmentManager().popBackStack();
            }
        });

        return consView;
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (5*60000 - millisUntilFinished) / 1000;
            progressBar.setProgress(progress);
            System.out.println("progress = " + progress);
        }

        @Override
        public void onFinish() {
            progressBar.setProgress(299);
            progressBar.setBackgroundColor(Color.GREEN);
            //Toast.makeText(getActivity(), "5 Minunten sind vorbei", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadConsList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<List<Consequence>> call = api.getCons(token, username);

        call.enqueue(new Callback<List<Consequence>>() {
            @Override
            public void onResponse(Call<List<Consequence>> call, retrofit2.Response<List<Consequence>> response) {
                if (response.isSuccessful()) {
                    consList = response.body();
                    if (consList == null || consList.isEmpty()) {
                        consListView.setEmptyView(consView.findViewById(R.id.consequence_listview));
                        Toast.makeText(getActivity(), "Keine Daten vorhanden", Toast.LENGTH_SHORT).show();
                    } else if (!consList.isEmpty()) {
                            consAdapter = new ConsequenceAdapter(getActivity(), consList);
                            System.out.println("------------- size: " + consList.size());
                            consListView.setAdapter(consAdapter);
                            consListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    openEditDialog(position);
                                }
                            });
                            consListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    removeDialog(consList.get(position).getId());
                                    //removeCons(consList.get(position).getId());
                                    return true;
                                }
                            });
                    }
                } else {
                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Consequence>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void saveCons(Consequence cons) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.saveCons(token, cons);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadConsList();
                } else {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void removeDialog(String id) {
        final String mId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wirklich löschen?");

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCons(mId);

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void removeCons(String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.removeCons(token, id);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadConsList();
                } else {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void openEditDialog(int position) {

        cons = consList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogCons = getActivity().getLayoutInflater().inflate(R.layout.dialog_consequence_add_edit, null);
        builder.setView(dialogCons);
        builder.setTitle("Bearbeiten");

        situation = (EditText) dialogCons.findViewById(R.id.consequence_dialog_situation);
        konsequenz = (EditText) dialogCons.findViewById(R.id.consequence_dialog_konsequenz);
        reaktion = (EditText) dialogCons.findViewById(R.id.consequence_dialog_reaktion);

        situation.setText(cons.getSituation());
        konsequenz.setText(cons.getKonsequenz());
        reaktion.setText(cons.getReaktion());

        builder.setCancelable(false)
                .setPositiveButton("Bearbeiten", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (situation.getText().toString().isEmpty() || konsequenz.getText().toString().isEmpty() || reaktion.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else if (situation.getText().toString().equals(cons.getSituation()) && konsequenz.getText().toString().equals(cons.getKonsequenz()) && reaktion.getText().toString().equals(cons.getReaktion())){
                            Toast.makeText(getActivity(), "Es hat sich nichts geändert", Toast.LENGTH_SHORT).show();
                        } else {
                            cons.setSituation(situation.getText().toString());
                            cons.setKonsequenz(konsequenz.getText().toString());
                            cons.setReaktion(reaktion.getText().toString());
                            saveCons(cons);
                            //loadConsList();
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

    public void addConsequenseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogCons = getActivity().getLayoutInflater().inflate(R.layout.dialog_consequence_add_edit, null);
        builder.setView(dialogCons);
        builder.setTitle("Hinzufügen");

        situation = (EditText) dialogCons.findViewById(R.id.consequence_dialog_situation);
        konsequenz = (EditText) dialogCons.findViewById(R.id.consequence_dialog_konsequenz);
        reaktion = (EditText) dialogCons.findViewById(R.id.consequence_dialog_reaktion);

        builder.setCancelable(false)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (situation.getText().toString().isEmpty() || konsequenz.getText().toString().isEmpty() || reaktion.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else {
                            newCons = new Consequence();
                            newCons.generateId();
                            newCons.setAutor(getUsername());
                            newCons.setSituation(situation.getText().toString());
                            newCons.setKonsequenz(konsequenz.getText().toString());
                            newCons.setReaktion(reaktion.getText().toString());
                            saveCons(newCons);
                            //loadConsList();
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

    public String getToken() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}
