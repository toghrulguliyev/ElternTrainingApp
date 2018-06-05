package at.ac.univie.entertain.elterntrainingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.LobenAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.Loben;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.service.PdfService.PdfHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;


public class LobenFragment extends Fragment {

    View lobenView;
    private List<Loben> lobenList;
    private Loben loben = new Loben();
    private Loben newLoben;
    private Button updBtn, goBackBtn, addBtn, savePdf;
    private SharedPreferences sharedPreferences;
    private LobenAdapter lobenAdapter;
    private ListView lobenListView;
    private EditText situation, art, reaktion;

    public LobenFragment() {
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
        lobenView = inflater.inflate(R.layout.fragment_loben, container, false);

        updBtn = (Button) lobenView.findViewById(R.id.loben_update);
        addBtn = (Button) lobenView.findViewById(R.id.loben_add);
        goBackBtn = (Button) lobenView.findViewById(R.id.loben_back_btn);
        lobenListView = (ListView) lobenView.findViewById(R.id.loben_list);
        savePdf = (Button) lobenView.findViewById(R.id.loben_savePdf_Btn);

        loadLobenList();

        updBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLobenList();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddLobenDialog();
            }
        });

        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStoragePermissionGranted()) {
                    if (!lobenList.isEmpty()) {
                        PdfHandler pdf = new PdfHandler(getToken(), getUsername());
                        try {
                            pdf.createLoben(lobenList, "Übung zum Loben des eigenen Kindes");
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

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return lobenView;
    }

    public void loadLobenList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<List<Loben>> call = api.getLoben(token, username);

        call.enqueue(new Callback<List<Loben>>() {
            @Override
            public void onResponse(Call<List<Loben>> call, retrofit2.Response<List<Loben>> response) {
                if (response.isSuccessful()) {
                    lobenList = response.body();
                    if (lobenList == null || lobenList.isEmpty()) {
                        Toast.makeText(getActivity(), "Keine Daten vorhanden", Toast.LENGTH_SHORT).show();
                        //lobenListView.setEmptyView(lobenView.findViewById(R.id.loben_list));
                        return;
                    } else {
                        if (!lobenList.isEmpty()) {
                            lobenAdapter = new LobenAdapter(getActivity(), lobenList);
                            lobenAdapter.notifyDataSetChanged();
                            lobenAdapter.notifyDataSetInvalidated();
                            lobenListView.setAdapter(lobenAdapter);
                            lobenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    openEditLobenDialog(position);
                                    lobenAdapter.notifyDataSetChanged();
                                }
                            });
                            lobenListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    //removeLoben(lobenList.get(position).getId());
                                    removeDialog(lobenList.get(position).getId());
                                    return true;
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Loben>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void openEditLobenDialog(int position) {

        loben = lobenList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogLoben = getActivity().getLayoutInflater().inflate(R.layout.dialog_loben_add_edit, null);
        builder.setView(dialogLoben);
        builder.setTitle("Bearbeiten");

        situation = (EditText) dialogLoben.findViewById(R.id.loben_dialog_situation);
        art = (EditText) dialogLoben.findViewById(R.id.loben_dialog_art);
        reaktion = (EditText) dialogLoben.findViewById(R.id.dialog_loben_reaktion);

        situation.setText(loben.getSituation());
        art.setText(loben.getArt());
        reaktion.setText(loben.getReaktion());

        builder.setCancelable(false)
                .setPositiveButton("Bearbeiten", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (situation.getText().toString().isEmpty() || art.getText().toString().isEmpty() || reaktion.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else if (situation.getText().toString().equals(loben.getSituation()) && art.getText().toString().equals(loben.getArt()) && reaktion.getText().toString().equals(loben.getReaktion())){
                            Toast.makeText(getActivity(), "Es hat sich nichts geändert", Toast.LENGTH_SHORT).show();
                        } else {
                            loben.setSituation(situation.getText().toString());
                            loben.setArt(art.getText().toString());
                            loben.setReaktion(reaktion.getText().toString());
                            saveLoben(loben);
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

    public void openAddLobenDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogLoben = getActivity().getLayoutInflater().inflate(R.layout.dialog_loben_add_edit, null);
        builder.setView(dialogLoben);
        builder.setTitle("Bearbeiten");

        situation = (EditText) dialogLoben.findViewById(R.id.loben_dialog_situation);
        art = (EditText) dialogLoben.findViewById(R.id.loben_dialog_art);
        reaktion = (EditText) dialogLoben.findViewById(R.id.dialog_loben_reaktion);

        builder.setCancelable(false)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (situation.getText().toString().isEmpty() || art.getText().toString().isEmpty() || reaktion.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else {
                            newLoben = new Loben();
                            newLoben.generateId();
                            newLoben.setAutor(getUsername());
                            newLoben.setSituation(situation.getText().toString());
                            newLoben.setArt(art.getText().toString());
                            newLoben.setReaktion(reaktion.getText().toString());
                            saveLoben(newLoben);
                            //loadLobenList();
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
                        removeLoben(mId);

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void removeLoben(String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.removeLoben(token, id);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadLobenList();
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

    public void saveLoben(Loben loben) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.saveLoben(token, loben);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadLobenList();
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
