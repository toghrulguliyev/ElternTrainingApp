package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.SimilarityAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.Emotions;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class EmotionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SharedPreferences sharedPreferences;
    private PieChart pieChartIst, pieChartSoll;
    View emoView;
    private String[] emotionNames = {"Traurigkeit", "Freude", "Angst", "Mut", "Ärger", "Angenehm", "Enttäusch", "Stolz"};
    private float[] emotionSoll = {20.0f, 80.0f, 10.0f, 90.0f, 30.0f, 70.0f, 5.0f, 95.0f}; //{0.05f, 0.2f, 0.025f, 0.225f, 0.075f, 0.175f, 0.0125f, 0.2375f};
    private ArrayList<PieEntry> istValues, sollValues;
    private Emotions istEmotions;
    private Button addEditEmotions, updateEmotionsBtn, compareBtn;
    private SeekBar seek1, seek2, seek3, seek4;
    private float[] istData = {50.0f, 50.0f, 50.0f, 50.0f, 50.0f, 50.0f, 50.0f, 50.0f};
    private float sollPercentage;
    private SwipeRefreshLayout swipeRefresh;
    private List<Emotions> emotionsList;
    private SimilarityAdapter simAdapter;
    private ListView emoListView;

    //TODO Test Similarity, add IST SOLL Similarity

    public EmotionsFragment() {
        // Required empty public constructor
    }

    public static EmotionsFragment newInstance(String param1, String param2) {
        EmotionsFragment fragment = new EmotionsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        sollPercentage = ((emotionSoll[0] + emotionSoll[2] + emotionSoll[4] + emotionSoll[6])/400);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        emoView = inflater.inflate(R.layout.fragment_emotions, container, false);

        swipeRefresh = emoView.findViewById(R.id.swipeRefreshEmotions);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));


        pieChartIst = (PieChart) emoView.findViewById(R.id.piechartIst);
        pieChartSoll = (PieChart) emoView.findViewById(R.id.piechartSoll);
        addEditEmotions = (Button) emoView.findViewById(R.id.piechart_add_edit_btn);
        updateEmotionsBtn = (Button) emoView.findViewById(R.id.piechart_update_btn);
        compareBtn = (Button) emoView.findViewById(R.id.piechart_compare_dialog_btn);

        pieChartSoll.setNoDataText("Keine Daten vorhanden");
        pieChartIst.setNoDataText("Keine Daten vorhanden. Bitten Daten Eingeben");

        updateEmotionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMyEmoState();
            }
        });

        addEditEmotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmotionsDialog();
            }
        });

        compareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSimilarity();
            }
        });

        setupSollChart();
        loadMyEmoState();


        return emoView;
    }

    public void loadMyEmoState() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();
        Call<Emotions> call = api.getEmotions(token, username);

        call.enqueue(new Callback<Emotions>() {
            @Override
            public void onResponse(Call<Emotions> call, retrofit2.Response<Emotions> response) {
                if (response.isSuccessful()) {
                    istEmotions = response.body();
                    System.out.println("--------------  IS SUCCESFUL  -------------");
                    if (istEmotions == null) {
                        Toast.makeText(getActivity(), "Keine Daten vorhanden. Bitte ein neuer Zustand eingeben", Toast.LENGTH_LONG).show();
                    } else {
                        istData = istEmotions.getEmotions();
                        for (int i = 0; i < istData.length; i++) {
                            System.out.println("--------- " + istData[i] + " --------");
                        }
                        setupIstChart();
                    }
                }
            }

            @Override
            public void onFailure(Call<Emotions> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void setupIstChart() {

        loadIstPieEntryList(istData);

        PieDataSet dataSetIst = new PieDataSet(istValues, "Meine Gefühle:");
        dataSetIst.setSliceSpace(1);
        dataSetIst.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData istData = new PieData(dataSetIst);
        istData.setValueFormatter(new PercentFormatter());
        istData.setValueTextSize(10);
        pieChartIst.setData(istData);

        pieChartIst.setUsePercentValues(true);
        pieChartIst.setHoleRadius(40f);
        pieChartIst.setTransparentCircleRadius(45f);
        pieChartIst.getDescription().setEnabled(false);
        //pieChartIst.getDescription().setEnabled(true);

        pieChartIst.setDrawSliceText(false);

        Legend leg = pieChartIst.getLegend();
        leg.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        leg.setTextSize(12f);

        pieChartIst.offsetLeftAndRight(0);
        pieChartIst.setExtraOffsets(35, 0, 0, 10);
        pieChartIst.invalidate();
        float similarity;
        if (((this.istData[0] + this.istData[2] + this.istData[4] + this.istData[6])/400) > sollPercentage) {
            similarity = sollPercentage / ((this.istData[0] + this.istData[2] + this.istData[4] + this.istData[6])/400);
            pieChartSoll.setHoleRadius(50f);
            pieChartSoll.setTransparentCircleRadius(55f);
            pieChartSoll.setCenterText("Similarität:" + String.format("%.2f",similarity*100)+"%");
            pieChartSoll.invalidate();
        } else {
            similarity = ((this.istData[0] + this.istData[2] + this.istData[4] + this.istData[6])/400) / sollPercentage;
            pieChartSoll.setHoleRadius(50f);
            pieChartSoll.setTransparentCircleRadius(55f);
            pieChartSoll.setCenterText("Similarität:" + String.format("%.2f",similarity*100)+"%");
            pieChartSoll.invalidate();
        }

    }

    public void setupSollChart() {
        sollValues = new ArrayList<>();
        for (int i = 0; i < emotionSoll.length; i++) {
            sollValues.add(new PieEntry(emotionSoll[i], emotionNames[i]));
        }
        PieDataSet dataSetSoll = new PieDataSet(sollValues, "");
        dataSetSoll.setSliceSpace(1);
        dataSetSoll.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData sollData = new PieData(dataSetSoll);
        sollData.setValueFormatter(new PercentFormatter());
        sollData.setValueTextSize(10);
        pieChartSoll.setData(sollData);
        pieChartSoll.setUsePercentValues(true);
        pieChartSoll.setHoleRadius(40f);
        pieChartSoll.setTransparentCircleRadius(45f);
        pieChartSoll.getDescription().setEnabled(false);
        pieChartSoll.setDrawSliceText(false);



        Legend legend = pieChartSoll.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        //pieChartSoll.setCenterText("Similarität: 90%");

        pieChartSoll.offsetLeftAndRight(0);
        pieChartSoll.setExtraOffsets(0, 0, 20, 0);
        pieChartSoll.invalidate();
    }

    public void loadIstPieEntryList(float[] emotions) {
        if (emotions == null || emotions.length < 1 || emotions.length < 8) {
            return;
        } else {
            istValues = new ArrayList<>();
            for (int i = 0; i < emotions.length; i++) {
                istValues.add(new PieEntry(emotions[i], emotionNames[i]));
            }
        }
    }

    public void openEmotionsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_edit_emotions, null);
        builder.setView(dialog);
        builder.setTitle("Emotionen eingeben");

        seek1 = (SeekBar) dialog.findViewById(R.id.seekBar_freude_traurigkeit);
        seek2 = (SeekBar) dialog.findViewById(R.id.seekBar2);
        seek3 = (SeekBar) dialog.findViewById(R.id.seekBar3);
        seek4 = (SeekBar) dialog.findViewById(R.id.seekBar4);


            seek1.setProgress(Math.round(istData[0]));
            seek2.setProgress(Math.round(istData[2]));
            seek3.setProgress(Math.round(istData[4]));
            seek4.setProgress(Math.round(istData[6]));

            seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    istData[0] = (float) progress;
                    istData[1] = (float) (100 - progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    istData[2] = (float) progress;
                    istData[3] = (float) (100 - progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seek3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    istData[4] = (float) progress;
                    istData[5] = (float) (100 - progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seek4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    istData[6] = (float) progress;
                    istData[7] = (float) (100 - progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        builder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveEmotions();
                                //loadMyEmoState();
                            }
                        }).setNegativeButton("Abbrechen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void showSimilarity() {
        //(istData[0] + istData[2] + istData[4] + istData[6])/400;
        //(emotionSoll[0] + emotionSoll[2] + emotionSoll[4] + emotionSoll[6])/400;

        if (istEmotions == null) {
            Toast.makeText(getActivity(), "IST Zustand ist nicht vorhanden. Bitte geben Sie die Daten ein", Toast.LENGTH_LONG).show();
            return;
        } else {
            openSimilarityDialog();
        }
    }

    public void openSimilarityDialog() {

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        View dialogSim = getActivity().getLayoutInflater().inflate(R.layout.dialog_emotions_similarity, null);
//        builder.setView(dialogSim);
//        builder.setTitle("Übereinstimmung");
//
//        emoListView = (ListView) dialogSim.findViewById(R.id.emotions_list_similarity);


        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<List<Emotions>> call = api.getFamilyEmotions(token, username);

        call.enqueue(new Callback<List<Emotions>>() {
            @Override
            public void onResponse(Call<List<Emotions>> call, retrofit2.Response<List<Emotions>> response) {
                if (response.isSuccessful()) {
                    emotionsList = new ArrayList<>();
                    emotionsList = response.body();
                    if (emotionsList == null || emotionsList.isEmpty()) {
                        Toast.makeText(getActivity(), "Keine Daten von Mitgliedern vorhanden", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        System.out.println("Emotionslist size: " + emotionsList.size());
                        simAdapter = new SimilarityAdapter(getActivity(), emotionsList, (istData[0] + istData[2] + istData[4] + istData[6])/400);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        View dialogSim = getActivity().getLayoutInflater().inflate(R.layout.dialog_emotions_similarity, null);
                        builder.setView(dialogSim);
                        builder.setTitle("Übereinstimmung");

                        emoListView = (ListView) dialogSim.findViewById(R.id.emotions_list_similarity);
                        emoListView.setAdapter(simAdapter);
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.show();

                    }
                }
            }


            @Override
            public void onFailure(Call<List<Emotions>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
//        if (emotionsList == null || emotionsList.isEmpty()) {
//            return;
//        } else if (emotionsList != null && !emotionsList.isEmpty()) {
//            builder.setCancelable(false)
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//            builder.show();
//        }
    }

    public void reallyOpenSimilarityDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogSim = getActivity().getLayoutInflater().inflate(R.layout.dialog_emotions_similarity, null);
        builder.setView(dialogSim);
        builder.setTitle("Übereinstimmung");

        emoListView = (ListView) dialogSim.findViewById(R.id.emotions_list_similarity);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();

    }


    public void saveEmotions() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<Response> call = api.saveEmotions(token, username, istData);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    loadMyEmoState();
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

    public String getFamilyId() {
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.FAMILY_ID,"");
    }

    @Override
    public void onRefresh() {
        loadMyEmoState();
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }
}
