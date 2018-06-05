package at.ac.univie.entertain.elterntrainingapp.service.FcmNotificationsService;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInstanceIdSerivce";
    private SharedPreferences sharedPreferences;

    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New Token " + refreshedToken);


        saveFcmToken(refreshedToken);
        saveUserFcmToken(refreshedToken);


    }

    private void saveUserFcmToken(String refreshedToken) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        Call<Response> call = api.saveFcmToken(getToken(), getUsername(), refreshedToken);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyFirebaseInstanceIdService.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MyFirebaseInstanceIdService.this, "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getToken() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.TOKEN_KEY,"");
    }

    public String getUsername() {
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }

    public void saveFcmToken(String fcmToken){
        sharedPreferences = getSharedPreferences(Const.FCM_TOKEN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FCM_TOKEN, fcmToken);
        editor.commit();
    }


}
