package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.FamilyRuleAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.FamilyRule;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class FamilyRuleFragment extends Fragment {

    private static final String ARG_FR = "familyRule";

    private ListView frListView;
    private FamilyRuleAdapter frAdapter;
    private SharedPreferences sharedPreferences;
    View frView;
    private Button addBtn, updateBtn;
    private List<FamilyRule> frList;
    private TextView ruleName;
    private TextView rule;
    private TextView reason;
    private TextView forWho;


//    private OnFragmentInteractionListener mListener;

    public FamilyRuleFragment() {
        // Required empty public constructor
    }

//    public void onResume(){
//        super.onResume();
//
//        // Set title bar
//        ((HomeActivity) getActivity())
//                .setActionBarTitle("Familienregeln");
//    }

    public static FamilyRuleFragment newInstance(String param1, String param2) {
        FamilyRuleFragment fragment = new FamilyRuleFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frView = inflater.inflate(R.layout.fragment_family_rule, container, false);
        addBtn = (Button) frView.findViewById(R.id.addRuleBtn);
        updateBtn = (Button) frView.findViewById(R.id.updRuleBtn);
        frListView = (ListView) frView.findViewById(R.id.family_rule_list);
        loadFamilyRules();
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFamilyRules();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addRule();
                openAddRule();
            }
        });

        return frView;
    }

    public void loadFamilyRules() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();
        String familyId = "";
        if (getFamilyId() != null && !getFamilyId().isEmpty()) {
            familyId = getFamilyId();
        }

        Call<List<FamilyRule>> call = api.getrules(token, username, familyId);

        call.enqueue(new Callback<List<FamilyRule>>() {
            @Override
            public void onResponse(Call<List<FamilyRule>> call, retrofit2.Response<List<FamilyRule>> response) {
                if (response.isSuccessful()) {
                    frList = new ArrayList<FamilyRule>();
                    frList = response.body();
                    if(frList == null || frList.isEmpty()) {
                        Toast.makeText(getActivity(), "Keine Regel vorhanden", Toast.LENGTH_SHORT).show();
                    } else if (!frList.isEmpty()) {
                            frAdapter = new FamilyRuleAdapter(getActivity(), frList);
                            frListView.setAdapter(frAdapter);
                        frListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                openRule(frList.get(position));
                            }
                        });

                        frListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                removeDialog(frList.get(position).getId());
                                return true;
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Keine Regel vorhanden", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FamilyRule>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler aufgetreten", Toast.LENGTH_SHORT).show();
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
                    removeFamilyRule(mId);

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void removeFamilyRule(String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.removeFr(token, id);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadFamilyRules();
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

    public void openRule(FamilyRule fr) {
        Fragment frInfoFragment = new FamilyRuleInfoFragment();
        Bundle args = new Bundle();
        if (fr != null) {
            args.putParcelable(ARG_FR, fr);
            frInfoFragment.setArguments(args);
        }
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_home, frInfoFragment).addToBackStack(null).commit();

    }

    public void addRule() {
        Fragment frItemFragment = new FamilyRuleItemFragment();
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_home, frItemFragment).addToBackStack(null).commit();
    }

    public void openAddRule() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogFr = getActivity().getLayoutInflater().inflate(R.layout.fragment_family_rule_item, null);
        builder.setView(dialogFr);
        builder.setTitle("Familienregel bearbeiten");

        ruleName = (TextView) dialogFr.findViewById(R.id.rule_name_input);
        rule = (TextView) dialogFr.findViewById(R.id.text_input_family_rule);
        reason = (TextView) dialogFr.findViewById(R.id.input_text_reason);
        forWho = (TextView) dialogFr.findViewById(R.id.input_text_forWho);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ruleName.getText().toString().isEmpty() || rule.getText().toString().isEmpty() || reason.getText().toString().isEmpty() || forWho.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else {
                            String nameRule = ruleName.getText().toString();
                            String theRule = rule.getText().toString();
                            String theForWho = forWho.getText().toString();
                            String theReason = reason.getText().toString();

                            FamilyRule fr = new FamilyRule(getUsername(), nameRule, theRule, theReason, theForWho);

                            if (getFamilyId() != null || !getFamilyId().isEmpty()) {
                                fr.setFamilyId(getFamilyId());
                            }

                            saveFamilyRule(fr);

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

    public void saveFamilyRule(FamilyRule fr) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.saveRule(token, fr);

        System.out.println("--------------api.saveRule(token, fr)--------------");

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                System.out.println("--------------OnResponse---------------");
                if (response.isSuccessful()) {
                    System.out.println("--------------IsSuccessful---------------");
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadFamilyRules();
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                System.out.println("--------------OnFailure---------------");
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

}
