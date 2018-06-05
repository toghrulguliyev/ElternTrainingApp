package at.ac.univie.entertain.elterntrainingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class FamilyRuleItemFragment extends Fragment {

    private static final String ARG_FR = "familyRule";

    private TextInputEditText ruleName;
    private TextInputEditText rule;
    private TextInputEditText reason;
    private TextInputEditText forWho;
    private Button saveBtn, backBtn;
    View frItemView;
    private FamilyRule familyRule;
    View familyRuleItemEditView;
//    AlertDialog dialog;
//    EditText editText;
    SharedPreferences sharedPreferences;

    public FamilyRuleItemFragment() {
        // Required empty public constructor
    }


    public void onResume(){
        super.onResume();

        // Set title bar
        ((HomeActivity) getActivity())
                .setActionBarTitle("Familienregel Bearbeiten");

    }

    public void toFamilyRuleFragment() {
        Fragment familyRuleFragment = new FamilyRuleFragment();
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_home, familyRuleFragment).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyRule = (FamilyRule) getArguments().getParcelable(ARG_FR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        familyRuleItemEditView = inflater.inflate(R.layout.fragment_family_rule_item, container, false);


        ruleName = familyRuleItemEditView.findViewById(R.id.rule_name_input);
        rule = familyRuleItemEditView.findViewById(R.id.text_input_family_rule);
        reason = familyRuleItemEditView.findViewById(R.id.input_text_reason);
        forWho = familyRuleItemEditView.findViewById(R.id.input_text_forWho);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFamilyRuleFragment();
            }
        });

        if (familyRule != null) {
            ruleName.setText(familyRule.getRuleName());
            rule.setText(familyRule.getRule());
            reason.setText(familyRule.getReason());
            forWho.setText(familyRule.getForWho());
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRule(checkData());
            }
        });


        return familyRuleItemEditView;

    }

    public void saveRule(boolean error) {
        if (error) {
            Toast.makeText(getActivity(), "Bitte, alle Daten eingeben", Toast.LENGTH_SHORT).show();
        } else {
            if (this.familyRule != null) {
                System.out.println("FamilyRule != null -----  " + this.familyRule.getId());
                if (!ruleName.getText().toString().equals(familyRule.getRuleName())) {
                    familyRule.setRuleName(ruleName.getText().toString());
                }
                if (!rule.getText().toString().equals(familyRule.getRule())) {
                    familyRule.setRule(rule.getText().toString());
                }
                if (!forWho.getText().toString().equals(familyRule.getForWho())) {
                    familyRule.setForWho(forWho.getText().toString());
                }
                if (!reason.getText().toString().equals(familyRule.getReason())) {
                    familyRule.setReason(reason.getText().toString());
                }

                Retrofit retrofit = RetrofitClient.getRetrofitClient();
                APIInterface api = retrofit.create(APIInterface.class);

                String token = getToken();
                //String username = getUsername();
                String familyId = "";

                System.out.println(familyRule.getAutor());

                if (getFamilyId() != null || !getFamilyId().isEmpty()) {
                    System.out.println(getFamilyId());
                    if (familyRule.getFamilyId() == null && familyRule.getFamilyId().isEmpty()) {
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

            } else {

                String nameRule = ruleName.getText().toString();
                String theRule = rule.getText().toString();
                String theForWho = forWho.getText().toString();
                String theReason = reason.getText().toString();

                Retrofit retrofit = RetrofitClient.getRetrofitClient();
                APIInterface api = retrofit.create(APIInterface.class);

                String token = getToken();
                String username = getUsername();
                String familyId = null;
                familyId = getFamilyId();

                FamilyRule fr = new FamilyRule(username, nameRule, theRule, theReason, theForWho);

                if (familyId != null || !familyId.isEmpty()) {
                    fr.setFamilyId(getFamilyId());
                }

                Call<Response> call = api.saveRule(token, fr);

                System.out.println("--------------api.saveRule(token, fr)--------------");

                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        System.out.println("--------------OnResponse---------------");
                        if (response.isSuccessful()) {
                            System.out.println("--------------IsSuccessful---------------");
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            toFamilyRuleFragment();
                        }
                    }
                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        System.out.println("--------------OnFailure---------------");
                        Toast.makeText(getActivity(), "Fehler aufgetreten", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public boolean checkData() {
        boolean error = false;
        if (ruleName.getText().toString().isEmpty()) {
            error = true;
            ruleName.setError("Bitte geben Sie den Regelname ein");
        }
        if (rule.getText().toString().isEmpty()) {
            error = true;
            rule.setError("Bitte geben Sie die Regel ein");
        }
        if (forWho.getText().toString().isEmpty()) {
            error = true;
            forWho.setError("Bitte geben Sie Daten hier ein");
        }
        if (reason.getText().toString().isEmpty()) {
            error = true;
            reason.setError("Bitte geben Sie den Grund ein");
        }
        return error;
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
