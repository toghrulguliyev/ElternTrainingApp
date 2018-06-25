package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.FamilyRule;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class FamilyRuleInfoFragment extends Fragment {

    private static final String ARG_FR = "familyRule";

    private TextView ruleName;
    private TextView rule;
    private TextView reason;
    private TextView forWho;
    private Button editBtn, backBtn;
    View frItemView;
    private FamilyRule familyRule;
    View familyRuleView;
    AlertDialog dialog;
    EditText editText;
    SharedPreferences sharedPreferences;


    public FamilyRuleInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.familyRule = (FamilyRule) getArguments().getParcelable(ARG_FR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        familyRuleView = inflater.inflate(R.layout.fragment_family_rule_info, container, false);

        ruleName = familyRuleView.findViewById(R.id.rule_name);
        rule = familyRuleView.findViewById(R.id.rule);
        reason = familyRuleView.findViewById(R.id.reason);
        forWho = familyRuleView.findViewById(R.id.for_who);
        editBtn = familyRuleView.findViewById(R.id.updateRuleBtn);
        backBtn = familyRuleView.findViewById(R.id.backRuleListBtn);

        if (familyRule != null) {
            ruleName.setText(familyRule.getRuleName());
            rule.setText(familyRule.getRule());
            reason.setText(familyRule.getReason());
            forWho.setText(familyRule.getForWho());
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toFamilyRuleFragment();
                getFragmentManager().beginTransaction().remove(FamilyRuleInfoFragment.this).commitAllowingStateLoss();
                getFragmentManager().popBackStack();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toFamilyRuleItemFragment();
                openEditDialog();
            }
        });


        return familyRuleView;
    }

    public void openEditDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogFr = getActivity().getLayoutInflater().inflate(R.layout.fragment_family_rule_item, null);
        builder.setView(dialogFr);
        builder.setTitle("Familienregel bearbeiten");

        ruleName = (TextView) dialogFr.findViewById(R.id.rule_name_input);
        rule = (TextView) dialogFr.findViewById(R.id.text_input_family_rule);
        reason = (TextView) dialogFr.findViewById(R.id.input_text_reason);
        forWho = (TextView) dialogFr.findViewById(R.id.input_text_forWho);

        if (familyRule != null) {
            ruleName.setText(familyRule.getRuleName());
            rule.setText(familyRule.getRule());
            reason.setText(familyRule.getReason());
            forWho.setText(familyRule.getForWho());
        }

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ruleName.getText().toString().isEmpty() || rule.getText().toString().isEmpty() || reason.getText().toString().isEmpty() || forWho.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                        } else if (ruleName.getText().toString().equals(familyRule.getRuleName()) && rule.getText().toString().equals(familyRule.getRule()) && reason.getText().toString().equals(familyRule.getReason()) && forWho.getText().toString().equals(familyRule.getForWho())){
                            Toast.makeText(getActivity(), "Es hat sich nichts geändert", Toast.LENGTH_SHORT).show();
                        } else {
                            familyRule.setRuleName(ruleName.getText().toString());
                            familyRule.setRule(rule.getText().toString());
                            familyRule.setReason(reason.getText().toString());
                            familyRule.setForWho(forWho.getText().toString());
                            saveFamilyRule(familyRule);
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
        String familyId = "";

        if (getFamilyId() != null || !getFamilyId().isEmpty()) {
            System.out.println(getFamilyId());
            if (familyRule.getFamilyId() == null || familyRule.getFamilyId().isEmpty()) {
                familyRule.setFamilyId(getFamilyId());
            }
        }

        Call<Response> call = api.saveRule(token, familyRule);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                System.out.println("--------OnResponse-----------");
                if (response.isSuccessful()) {
                    System.out.println("---------isSuccessful---------");
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    toFamilyRuleFragment();
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                System.out.println("--------onFailure------");
                Toast.makeText(getActivity(), "Fehler aufgetreten", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void toFamilyRuleFragment() {
        Fragment familyRuleFragment = new FamilyRuleFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_home, familyRuleFragment).commit();
    }

    public void toFamilyRuleItemFragment() {
        Fragment familyRuleItemFragment = new FamilyRuleItemFragment();
        Bundle args = new Bundle();
        if (familyRule != null) {
            args.putParcelable(ARG_FR, familyRule);
        }
        familyRuleItemFragment.setArguments(args);
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_home, familyRuleItemFragment).addToBackStack(null).commit();
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
