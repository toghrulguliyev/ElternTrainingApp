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
import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.GedankeAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.Gedanke;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.service.PdfService.PdfHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;


public class DysfunctionalFragment extends Fragment {


    View dysView;
    private List<Gedanke> gedanken;
    private Gedanke gedanke = new Gedanke();
    private Gedanke newGedanke;
    private Button updBtn, goBackBtn, addBtn, savePdf;
    private SharedPreferences sharedPreferences;
    private GedankeAdapter gedankeAdapter;
    private ListView gedankenListView;
    private EditText situation, bewertung, feel, altBewertung, altReaktion;
    private ProgressBar progressBar;


    public DysfunctionalFragment() {
        // Required empty public constructor
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
        dysView = inflater.inflate(R.layout.fragment_dysfunctional, container, false);

        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        updBtn = (Button) dysView.findViewById(R.id.dys_upd_btn);
        goBackBtn = (Button) dysView.findViewById(R.id.dys_backBtn);
        addBtn = (Button) dysView.findViewById(R.id.dys_addBtn);
        gedankenListView = (ListView) dysView.findViewById(R.id.dys_gedanken_lv);
        savePdf = (Button) dysView.findViewById(R.id.dys_savePdf_Btn);
        progressBar = (ProgressBar) dysView.findViewById(R.id.progressBar_dys);

        loadGedanken();

        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    if (!gedanken.isEmpty()) {
                        PdfHandler pdf = new PdfHandler(getToken(), getUsername(), getActivity());
                        try {
                            pdf.createGedanke(gedanken, "Übung zum Einfluss dysfunktionaler Gedanken auf die Erziehung");
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

        updBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGedanken();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGedankeDialog();
            }
        });

        progressBar.setMax(299);
        progressBar.setProgress(299);
        MyCountDownTimer countDownTimer = new MyCountDownTimer(5*60000, 1000);
        countDownTimer.start();

        return dysView;
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
            Toast.makeText(getActivity(), "5 Minunten sind vorbei", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadGedanken() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<List<Gedanke>> call = api.getMyGedanke(token, username);

        call.enqueue(new Callback<List<Gedanke>>() {
            @Override
            public void onResponse(Call<List<Gedanke>> call, retrofit2.Response<List<Gedanke>> response) {
                if (response.isSuccessful()) {
                    gedanken = new ArrayList<Gedanke>();
                    gedanken = response.body();
                    if (gedanken == null || gedanken.isEmpty()) {
                        Toast.makeText(getActivity(), "Keine Daten vorhanden", Toast.LENGTH_SHORT).show();
                        gedankenListView.setEmptyView(dysView.findViewById(R.id.dys_gedanken_lv));
                        return;
                    } else if (!gedanken.isEmpty()) {
                        gedankeAdapter = new GedankeAdapter(getActivity(), gedanken);
                        gedankenListView.setAdapter(gedankeAdapter);
                        gedankenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                openEditGedankeDialog(position);
                            }
                        });
                        gedankenListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                removeDialog(gedanken.get(position).getId());
                                //removeGedanke(gedanken.get(position).getId());
                                return true;
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Anfrage ist fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Gedanke>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openEditGedankeDialog(int position) {

        gedanke = gedanken.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogGedanke = getActivity().getLayoutInflater().inflate(R.layout.dialog_dysf_gedanke_add_edit, null);
        builder.setView(dialogGedanke);
        builder.setTitle("Gedanke bearbeiten");

        situation = (EditText) dialogGedanke.findViewById(R.id.dys_situation_dialog);
        bewertung = (EditText) dialogGedanke.findViewById(R.id.dys_bewertung_dialog);
        altBewertung = (EditText) dialogGedanke.findViewById(R.id.dys_alt_bewert_dialog);
        altReaktion = (EditText) dialogGedanke.findViewById(R.id.dys_alt_reakt_dialog);
        feel = (EditText) dialogGedanke.findViewById(R.id.dys_feel_dialog);

        situation.setText(gedanke.getSituation());
        bewertung.setText(gedanke.getBewertung());
        altBewertung.setText(gedanke.getAltBewertung());
        altReaktion.setText(gedanke.getAltReaktion());
        feel.setText(gedanke.getFeel());


        builder.setCancelable(false)
                .setPositiveButton("Bearbeiten", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (situation.getText().toString().isEmpty() || bewertung.getText().toString().isEmpty() || altBewertung.getText().toString().isEmpty() || altReaktion.getText().toString().isEmpty() || feel.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else if (situation.getText().toString().equals(gedanke.getSituation()) && bewertung.getText().toString().equals(gedanke.getBewertung()) && altBewertung.getText().toString().equals(gedanke.getAltBewertung()) && altReaktion.getText().toString().equals(gedanke.getAltReaktion()) && feel.getText().toString().equals(gedanke.getFeel())) {
                            Toast.makeText(getActivity(), "Es hat sich nichts geändert", Toast.LENGTH_SHORT).show();
                        } else {
                            gedanke.setSituation(situation.getText().toString());
                            gedanke.setBewertung(bewertung.getText().toString());
                            gedanke.setAltBewertung(altBewertung.getText().toString());
                            gedanke.setAltReaktion(altReaktion.getText().toString());
                            gedanke.setFeel(feel.getText().toString());
                            saveGedanke(gedanke);
                            //loadGedanken();
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

    public void removeDialog(String id) {

        final String mId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wirklich löschen?");

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeGedanke(mId);

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void removeGedanke(String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<Response> call = api.removeGedanke(token, username, id);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadGedanken();
                } else {
                    Toast.makeText(getActivity(), "Nicht gelöscht", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addGedankeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogGedanke = getActivity().getLayoutInflater().inflate(R.layout.dialog_dysf_gedanke_add_edit, null);
        builder.setView(dialogGedanke);
        builder.setTitle("Gedanke hinzufügen");

        situation = (EditText) dialogGedanke.findViewById(R.id.dys_situation_dialog);
        bewertung = (EditText) dialogGedanke.findViewById(R.id.dys_bewertung_dialog);
        altBewertung = (EditText) dialogGedanke.findViewById(R.id.dys_alt_bewert_dialog);
        altReaktion = (EditText) dialogGedanke.findViewById(R.id.dys_alt_reakt_dialog);
        feel = (EditText) dialogGedanke.findViewById(R.id.dys_feel_dialog);


        builder.setCancelable(false)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (situation.getText().toString().isEmpty() || bewertung.getText().toString().isEmpty() || altBewertung.getText().toString().isEmpty() || altReaktion.getText().toString().isEmpty() || feel.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else {
                            newGedanke = new Gedanke();
                            newGedanke.generateId();
                            newGedanke.setAutor(getUsername());
                            newGedanke.setSituation(situation.getText().toString());
                            newGedanke.setBewertung(bewertung.getText().toString());
                            newGedanke.setAltBewertung(altBewertung.getText().toString());
                            newGedanke.setAltReaktion(altReaktion.getText().toString());
                            newGedanke.setFeel(feel.getText().toString());

                            saveGedanke(newGedanke);
                            //loadGedanken();
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

    public void saveGedanke(Gedanke gedanke) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        System.out.println(gedanke.getAutor());
        System.out.println(gedanke.getSituation());
        System.out.println(gedanke.getBewertung());
        System.out.println(gedanke.getAltBewertung());
        System.out.println(gedanke.getAltReaktion());
        System.out.println(gedanke.getFeel());

        Call<Response> call = api.saveGedanke(token, gedanke);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadGedanken();
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

    public String getToken() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//
//    }

//    @Override
//    public void onStart() {
//
//        int orientation = getResources().getConfiguration().orientation;
//
//        if (orientation == 2) {
//            Intent serviceIntent = new Intent(getActivity(), ELMScanner.class);
//            getActivity().getApplicationContext().bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
//        }
//        super.onStart();
//    }
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
