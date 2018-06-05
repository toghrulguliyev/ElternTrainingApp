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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.Selfportrait;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.service.PdfService.PdfHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;


public class SelfportraitFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;

    private static final String ARG_PARAM1 = "param1";

    private ListView strengthLv, weaknessLv;
    private SharedPreferences sharedPreferences;
    private ArrayAdapter<String> strengthAdapter, weaknessAdapter;
    private List<String> strengths, weaknesses;
    View sfView;
    private Button addQualityBtn, goBackBtn, saveToPdf;
    private EditText addQuality, editQuality;
    private Selfportrait sp;
    private Switch qualityType;
    private boolean staerke;
    private int check = 0;

    private int position;

    public SelfportraitFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SelfportraitFragment newInstance(String param1, String param2) {
        SelfportraitFragment fragment = new SelfportraitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

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
        sfView = inflater.inflate(R.layout.fragment_selfportrait, container, false);

        strengthLv = (ListView) sfView.findViewById(R.id.selfportrait_strength_list);
        weaknessLv = (ListView) sfView.findViewById(R.id.selfportrait_weakness_list);
        addQualityBtn = (Button) sfView.findViewById(R.id.addQualityBtn);
        goBackBtn = (Button) sfView.findViewById(R.id.selfportrait_backBtn);
        saveToPdf = (Button) sfView.findViewById(R.id.selfportrait_saveToPdfBtn);
        loadQualitiesLists();

        addQualityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewQuality();
                loadQualitiesLists();
            }
        });

        saveToPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStoragePermissionGranted()) {
                    if (!sp.getStrengths().isEmpty() && !sp.getWeaknesses().isEmpty()) {
                        PdfHandler pdf = new PdfHandler(getToken(), getUsername());
                        try {
                            pdf.createSf(sp.getStrengths(), sp.getWeaknesses(), "Übung zum Einfluss dysfunktionaler Gedanken auf die Erziehung");
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

        return sfView;
    }

    public void addNewQuality() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogPwd = getActivity().getLayoutInflater().inflate(R.layout.add_quality_dialog, null);
        builder.setView(dialogPwd);
        addQuality = (EditText) dialogPwd.findViewById(R.id.selfportrait_add_quality);
        qualityType = (Switch) dialogPwd.findViewById(R.id.switch1);
        qualityType.setChecked(true);
        if (qualityType != null) {
            qualityType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        staerke = false;
                    } else {
                        staerke = true;
                    }
                    System.out.println("-------------" + staerke + "--------------- 117");
                }
            });
        }

        builder.setTitle("Neue Eigenschaft hinzufügen");
        builder.setCancelable(false)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("-------------" + staerke + "--------------- 128");
                        if (addQuality.getText().toString().isEmpty()) {
                            addQuality.setError("Eigenschaft eingeben");
                        } else {
                            if (sp == null) {
                                if (staerke) {
                                    System.out.println("-------------" + staerke + "--------------- 134");
                                    sp = new Selfportrait(getUsername());
                                    sp.setStrengths(new ArrayList<String>());
                                    sp.addStrength(addQuality.getText().toString());
                                } else {
                                    System.out.println("-------------" + staerke + "--------------- 139");
                                    sp = new Selfportrait(getUsername());
                                    sp.setWeaknesses(new ArrayList<String>());
                                    sp.addWeakness(addQuality.getText().toString());
                                }
                            } else {
                                if (staerke) {
                                    System.out.println("-------------" + staerke + "--------------- 146");
                                    System.out.println("--------STAERKE - STRENGTHS ----------------");
                                    sp.addStrength(addQuality.getText().toString());
                                } else {
                                    System.out.println("-------------" + staerke + "--------------- 150");
                                    sp.addWeakness(addQuality.getText().toString());
                                }
                            }

                            Retrofit retrofit = RetrofitClient.getRetrofitClient();
                            APIInterface api = retrofit.create(APIInterface.class);

                            String token = getToken();

                            Call<Response> call = api.saveQualities(token, sp);
                            call.enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadQualitiesLists();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
                                }
                            });
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



    public void loadQualitiesLists() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<Selfportrait> call = api.getQuality(token, username);

        call.enqueue(new Callback<Selfportrait>() {
            @Override
            public void onResponse(Call<Selfportrait> call, retrofit2.Response<Selfportrait> response) {
                sp = response.body();
                if (sp == null) {
                    Toast.makeText(getActivity(), "Keine Daten vorhanden", Toast.LENGTH_LONG).show();
                } else {
                    if (sp.getStrengths() == null) {
                        Toast.makeText(getActivity(), "Keine Stärke vorhanden", Toast.LENGTH_LONG).show();
                    }
                    if (sp.getWeaknesses() == null) {
                        Toast.makeText(getActivity(), "Keine Schwäche vorhanden", Toast.LENGTH_SHORT).show();
                    }
                    if (sp.getWeaknesses().size() > 0 && sp.getStrengths().size() > 0) {
                        weaknessAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sp.getWeaknesses());
                        weaknessLv.setAdapter(weaknessAdapter);
                        weaknessAdapter.notifyDataSetChanged();
                        if (sp.getWeaknesses().size() > 3) {
                            weaknessLv.setFastScrollAlwaysVisible(true);
                        }
                        weaknessLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getEditDialog(position, false);
                            }
                        });
                        weaknessLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                getDeleteDialog(position, false);
                                return true;
                            }
                        });
                        strengthAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sp.getStrengths());
                        strengthAdapter.notifyDataSetChanged();
                        strengthLv.setAdapter(strengthAdapter);
                        if (sp.getStrengths().size() > 3) {
                            strengthLv.setFastScrollAlwaysVisible(true);
                        }
                        strengthLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                getDeleteDialog(position, true);
                                return true;
                            }
                        });
                        strengthLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getEditDialog(position, true);
                            }
                        });
                    }
                    if (sp.getStrengths().size() > 0 && (sp.getWeaknesses() == null || sp.getWeaknesses().size() < 1)) {
                        for (int i = 0; i < sp.getWeaknesses().size(); i++) {
                            System.out.println("------------" + sp.getStrengths().get(i) + "---------");
                        }
                        strengthAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sp.getStrengths());
                        strengthLv.setAdapter(strengthAdapter);
                        if (sp.getStrengths().size() > 3) {
                            strengthLv.setFastScrollAlwaysVisible(true);
                        }
                        strengthLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                getDeleteDialog(position, true);
                                return true;
                            }
                        });
                        strengthLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getEditDialog(position, true);
                            }
                        });
                    }
                    if (sp.getWeaknesses().size() > 0 && (sp.getStrengths() == null || sp.getStrengths().size() == 0)) {
                        for (int i = 0; i < sp.getWeaknesses().size(); i++) {
                            System.out.println("------------" + sp.getWeaknesses().get(i) + "---------");
                        }
                        weaknessAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sp.getWeaknesses());
                        weaknessLv.setAdapter(weaknessAdapter);
                        if (sp.getWeaknesses().size() > 3) {
                            weaknessLv.setFastScrollAlwaysVisible(true);
                        }
                        weaknessLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                getDeleteDialog(position, false);
                                return true;
                            }
                        });
                        weaknessLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getEditDialog(position, false);
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<Selfportrait> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDeleteDialog(int pos, boolean quality) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wirklich löschen?");

        if (quality) {
            sp.getStrengths().remove(pos);
        } else {
            sp.getWeaknesses().remove(pos);
        }

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Retrofit retrofit = RetrofitClient.getRetrofitClient();
                        APIInterface api = retrofit.create(APIInterface.class);

                        String token = getToken();

                        Call<Response> call = api.saveQualities(token, sp);

                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    loadQualitiesLists();
                                }
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {
                                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void getEditDialog(int pos, boolean quality) {

        position = pos;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogPwd = getActivity().getLayoutInflater().inflate(R.layout.edit_quality_item, null);
        builder.setView(dialogPwd);
        editQuality = (EditText) dialogPwd.findViewById(R.id.selfportrait_edit_quality);
        if (quality) {
            builder.setTitle("Stärke bearbeiten");
            editQuality.setText(sp.getStrengths().get(position));
            builder.setCancelable(false)
                    .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            sp.getStrengths().set(position, editQuality.getText().toString());

                            Retrofit retrofit = RetrofitClient.getRetrofitClient();
                            APIInterface api = retrofit.create(APIInterface.class);

                            String token = getToken();

                            Call<Response> call = api.saveQualities(token, sp);
                            call.enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadQualitiesLists();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            builder.setTitle("Schwäche bearbeiten");
            System.out.println("----------" + sp.getWeaknesses().get(position) + "--------------");
            editQuality.setText(sp.getWeaknesses().get(position));
            builder.setCancelable(false)
                    .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            sp.getWeaknesses().set(position, editQuality.getText().toString());

                            Retrofit retrofit = RetrofitClient.getRetrofitClient();
                            APIInterface api = retrofit.create(APIInterface.class);

                            String token = getToken();

                            Call<Response> call = api.saveQualities(token, sp);
                            call.enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadQualitiesLists();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

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

    @Override
    public void onRefresh() {
        loadQualitiesLists();
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
