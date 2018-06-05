package at.ac.univie.entertain.elterntrainingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.service.FcmNotificationsService.MyFirebaseInstanceIdService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(getFamilyId());
        System.out.println("test----");
        if(checkToken() && checkUsername()){

            subscribe();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            this.finish();
        }else {
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

    public boolean checkUsername(){
        boolean res = false;
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        String username = sharedPreferences.getString(Const.USERNAME_KEY    ,"");
        if(!username.isEmpty()){
            res = true;
        }
        return res;
    }

    public void saveFcmToken(String fcmToken){
        sharedPreferences = getSharedPreferences(Const.FCM_TOKEN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FCM_TOKEN, fcmToken);
        editor.commit();
    }

    public String getToken() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
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
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getFamilyId() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.FAMILY_ID,"");
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
            System.out.println(getFamilyId());
            FirebaseMessaging.getInstance().subscribeToTopic(getFamilyId());
        }

    }

}
