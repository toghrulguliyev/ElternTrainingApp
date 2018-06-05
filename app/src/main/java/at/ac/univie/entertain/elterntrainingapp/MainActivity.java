package at.ac.univie.entertain.elterntrainingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String fcmToken;
    private String familyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkToken() && checkUsername()){

            subscribe();

            System.out.println("Home familyId: " + getFamilyId());

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            setContentView(R.layout.activity_main);
        }
        setContentView(R.layout.activity_main);
    }



    public void goToLogin(View view) {
        Intent Intent = new Intent(this, LoginActivity.class);
        startActivity(Intent);
    }

    public void goToRegister(View view) {
        Intent Intent = new Intent(this, RegisterActivity.class);
        startActivity(Intent);
    }

    public boolean checkToken(){
        boolean res = false;
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        String token = sharedPreferences.getString(Const.TOKEN_KEY,"");
        if(!token.isEmpty()){
            res = true;
        }
        return res;
    }

    public boolean checkUsername() {
        boolean res = false;
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        String username = sharedPreferences.getString(Const.USERNAME_KEY    ,"");
        if(!username.isEmpty()){
            res = true;
        }
        return res;
    }

    public void saveFcmToken(String fcmToken){
        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FCM_TOKEN, fcmToken);
        editor.commit();
    }

    public String getToken() {
        sharedPreferences = this.getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

    private void saveUserFcmToken(String refreshedToken) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        Call<Response> call = api.saveFcmToken(getToken(), getUsername(), refreshedToken);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, Throwable t) {
                System.out.println("Failure - MainActivity - saveUserFcmToken");
                Toast.makeText(MainActivity.this, "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getFamilyId() {
        sharedPreferences = this.getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(Const.FAMILY_ID,"");
    }

    public void getUserFamilyId() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        Call<User> call = api.getUser(getToken(), getUsername());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull retrofit2.Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        if (user.getFamilyId() != null && !user.getFamilyId().isEmpty()) {
                            familyId = user.getFamilyId();
                            saveFamilyId(familyId);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {

            }
        });
    }

    public void saveFamilyId(String familyId) {
        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FAMILY_ID, familyId);
        editor.commit();
    }

    public void subscribe() {

        FirebaseMessaging.getInstance().subscribeToTopic("nachrichten");
        fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken != null) {
            saveFcmToken(fcmToken);
            saveUserFcmToken(fcmToken);
            System.out.println("FCM_TOKEN: " + fcmToken);
        } else {
            System.out.println("Token is not obtained");
        }

        if (getFamilyId() != null && !getFamilyId().isEmpty()) {
            FirebaseMessaging.getInstance().subscribeToTopic(getFamilyId());
        } else if (getFamilyId() == null || getFamilyId().isEmpty()) {
            getUserFamilyId();
            if (familyId != null && !familyId.isEmpty()) {
                System.out.println(getFamilyId());
                FirebaseMessaging.getInstance().subscribeToTopic(getFamilyId());
            }
        }
    }

}
