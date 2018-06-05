package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

public class AccountSettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    View accountView;
    private TextView accountVorname, accountNachname, accountEmail, accountUsername, accountType;
    private Button changePwd, addFriends;
    private EditText newPwd;
    private EditText confPwd;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onResume(){
        super.onResume();

        // Set title bar
        ((HomeActivity) getActivity())
                .setActionBarTitle("Konto Einstellungen");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        accountView = inflater.inflate(R.layout.fragment_account_settings, container, false);

        accountVorname = accountView.findViewById(R.id.accountUserVorname);
        accountNachname = accountView.findViewById(R.id.accountUserNachname);
        accountEmail = accountView.findViewById(R.id.accountUserEmail);
        accountUsername = accountView.findViewById(R.id.accountUserUsername);
        accountType = accountView.findViewById(R.id.accountUserKonto);

        changePwd = accountView.findViewById(R.id.change_password);
        addFriends = accountView.findViewById(R.id.addFriendBtn);

        loadUserData();

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();
            }
        });

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFriendsFragment();
            }
        });

        return accountView;
    }

    public void toFriendsFragment() {
        Fragment friendsFragment = new FriendsFragment();
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_home, friendsFragment).commit();
    }

    public void loadUserData() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();
        Call<User> call = api.getUser(token, username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    accountVorname.setText(user.getFirstName());
                    accountNachname.setText(user.getLastName());
                    accountEmail.setText(user.getEmail());
                    accountUsername.setText(user.getUsername());
                    if (user.getAccType() == 1) {
                        accountType.setText("Kind");
                    } else if (user.getAccType() == 2) {
                        accountType.setText("Vater");
                    } else if (user.getAccType() == 3) {
                        accountType.setText("Mutter");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Zugriff gesperrt", Toast.LENGTH_SHORT).show();
                //}
            }
        });
    }

    public void changePasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogPwd = getActivity().getLayoutInflater().inflate(R.layout.new_password_dialog, null);
        builder.setView(dialogPwd);
        builder.setTitle("Neues Passwort");
        //builder.setIcon(R.drawable.key_icon;

        newPwd = dialogPwd.findViewById(R.id.dialog_new_pwd);
        confPwd = dialogPwd.findViewById(R.id.dialog_conf_pwd);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (newPwd.getText().toString().isEmpty() || confPwd.getText().toString().isEmpty()) {
//                            newPwd.setError("Passwort eingeben!");
//                            confPwd.setError("Passwort bestätigen!");
                            Toast.makeText(getActivity(), "Beide Felder ausfüllen!", Toast.LENGTH_LONG).show();
                        } else if (!newPwd.getText().toString().equals(confPwd.getText().toString())) {
                            Toast.makeText(getActivity(), "Eingegebene Passwörter stimmen nicht überein!", Toast.LENGTH_LONG).show();
                        } else {
                            changePassword(newPwd.getText().toString());
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

    public void changePassword(String newPassword) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();
        Call<Response> call = api.changePwd(token, username, newPassword);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Zugriff gesperrt", Toast.LENGTH_SHORT).show();
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
