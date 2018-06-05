package at.ac.univie.entertain.elterntrainingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Login;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.service.FcmNotificationsService.MyFirebaseInstanceIdService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText usernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences sharedPreferences;
    private String fcmToken;
    private String familyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        usernameView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

//        mLoginFormView = findViewById(R.id.login_form);
//        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String password = mPasswordView.getText().toString();


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Passwort ist zu kurz!");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.

        if (TextUtils.isEmpty(username)) {
            usernameView.setError("Bitte geben Sie ihr Username ein!");
            focusView = usernameView;
            cancel = true;
        }
        /*
        else if (!isEmailValid(email)) {
            mEmailView.setError("Ungültige Email Adresse!");
            focusView = mEmailView;
            cancel = true;
        }
        */
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            loginProcess(new Login(username, password));
        }
    }

//    private boolean isEmailValid(CharSequence target) {
//        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
//    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void loginProcess(Login login) {

        Gson gson = new Gson();
        String json = gson.toJson(login);

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        Call<Response> call = api.login(login);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    String token = response.headers().get(Const.TOKEN_KEY);
                    //TODO CHECK IF NO NULLPOINTERS
                    familyId = response.headers().get(Const.FAMILY_ID);
                    if (familyId != null) {
                        System.out.println("FamilyId: " + familyId);
                        saveFamilyId(familyId);
                    }
                    saveToken(token);
                    String username = response.headers().get("username");
                    saveUsername(username);
                    //Saving notification's data
                    //FirebaseMessaging.getInstance().subscribeToTopic("nachrichten")
                    FirebaseMessaging.getInstance().subscribeToTopic("nachrichten")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Allgemeine Benachrichtungen sind erfolgreich eingeschaltet";
                                    if (!task.isSuccessful()) {
                                        msg = "Allgemeine Benachrichtigungen sind fehlgeschlagen";
                                    }
                                    Log.d("MainActivity", msg);
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                    fcmToken = FirebaseInstanceId.getInstance().getToken();
                    if (fcmToken != null) {
                        saveFcmToken(fcmToken);
                        saveUserFcmToken(fcmToken, username, fcmToken);
                        Log.d("LoginActivity","FCM_TOKEN: " + fcmToken);
                    } else {
                        Log.d("LoginActivity", "Token is not obtained");
                    }

                    if (familyId != null && !familyId.isEmpty()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(familyId);
                    }

//                    fcmToken = FirebaseInstanceId.getInstance().getToken();
//                    if (fcmToken != null) {
//                        saveFcmToken(fcmToken);
//                        saveUserFcmToken(token, username, fcmToken);
//                    }
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    goTo("welcome");
                } else {
                    Toast.makeText(LoginActivity.this, "Username oder Passwort ist nicht korrekt", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fehler auf dem Server!", Toast.LENGTH_SHORT).show();
                goTo("main");
            }
        });

    }

    public void goTo(String activity){

        Intent intent;
        if(activity.equalsIgnoreCase("welcome")){
            intent = new Intent(this, HomeActivity.class);

        }else{
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        this.finish();
    }

    public void saveToken(String token) {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.TOKEN_KEY, token);
        editor.commit();
    }

    public void saveUsername(String username){
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.USERNAME_KEY, username);
        editor.commit();
    }

    public void saveFamilyId(String familyId) {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FAMILY_ID, familyId);
        editor.commit();
    }

    public void saveFcmToken(String fcmToken) {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FCM_TOKEN, fcmToken);
        editor.commit();
    }

    private void saveUserFcmToken(String springToken, String username, String refreshedToken) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        Call<Response> call = api.saveFcmToken(springToken, username, refreshedToken);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, Throwable t) {
                System.out.println("Failure - LoginActivity - saveUserFcmToken");
                Toast.makeText(LoginActivity.this, "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

}