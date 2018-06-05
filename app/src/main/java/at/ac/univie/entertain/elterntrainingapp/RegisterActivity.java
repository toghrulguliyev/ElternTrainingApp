package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText firstName, lastName, username, email, password, confPassword, birthday;
    private RadioGroup gender;
    private TextView spinnerTxt;
    private Button regBtn;
    private int day, month, year;
    private String genderToSave;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private Spinner spinner;
    private int accType;
    private static final String[] accTypes = {"Kind", "Vater", "Mutter"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confPassword = (EditText) findViewById(R.id.confPswd);
        birthday = (EditText) findViewById(R.id.birthday);
        spinner = (Spinner) findViewById(R.id.spinner_family);
        spinnerTxt = (TextView) findViewById(R.id.who_are_you);
        gender = (RadioGroup) findViewById(R.id.input_gender);
        regBtn = (Button) findViewById(R.id.registerMeBtn);

        //Test input TYpe
        birthday.setShowSoftInputOnFocus(false);

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //boolean error = checkData();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                alertDialogBuilder.setTitle("Anmeldung");
                alertDialogBuilder.setMessage("Möchten Sie sich wirklich registrieren?");
                alertDialogBuilder.setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Anmeldung abgebrochen!", Toast.LENGTH_LONG).show();
                            }
                        });
                alertDialogBuilder.setPositiveButton("Anmelden",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitData(checkData());

                            }
                        });
                alertDialogBuilder.show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                this.accType = 1;
                break;
            case 1:
                this.accType = 2;
                break;
            case 2:
                this.accType = 3;
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private boolean checkData() {

        boolean error = false;

        if(firstName.getText().toString().isEmpty()) {
            error = true;
            System.out.println("firstName");
            firstName.setError("Bitte Vorname eingeben!");
        }
        if(lastName.getText().toString().isEmpty()){
            error = true;
            System.out.println("lastName");
            lastName.setError("Bitte Nachname eingeben!");
        }
        if(username.getText().toString().isEmpty()){
            error = true;
            System.out.println("username");
            username.setError("Bitte Username eingeben!");
        }
        if(email.getText().toString().isEmpty()){
            error = true;
            System.out.println("email");
            email.setError("Bitte Email Adresse eingeben!");

        }
        if(!isValidEmail(email.getText().toString())){
            error = true;
            System.out.println("EmailIsNotValid");
            email.setError("Bitte geben Sie eine passende Email Adresse ein!");
        }
        if(password.getText().toString().isEmpty()){
            error = true;
            System.out.println("password");
            password.setError("Bitte geben Sie ein Passwort ein!");
        }
        if(confPassword.getText().toString().isEmpty()){
            error = true;
            System.out.println("confPassword");
            confPassword.setError("Bitte bestätigen Sieihr Passwort!");
        }
        if(!password.getText().toString().equals(confPassword.getText().toString())){
            error = true;
            System.out.println("Passwoerte sind Falsch");
            confPassword.setError("Irgendwas passt nicht!");
            Toast.makeText(this, "Passwörter stimmen nicht überein!", Toast.LENGTH_SHORT).show();
        }
        if(birthday.getText().toString().isEmpty()){
            error = true;
            System.out.println("birthday");
            birthday.setError("Bitte geben Sie ihr Geburtstag ein");
        }
        if(spinner.getSelectedItem().toString() == null || spinner.getSelectedItem().toString().isEmpty()) {
            error = true;
            System.out.println("Spinner");
            spinnerTxt.setTextColor(Color.RED);
        }

        int selected = gender.getCheckedRadioButtonId();
        //String gender;
        if(selected == R.id.femaleBtn) {
            genderToSave = "female";
        } else {
            genderToSave = "male";
        }
        if(genderToSave.isEmpty()) {
            error = true;
            System.out.println("gender");
        }
        return error;
    }

    public void submitData(boolean error) {
        if(error) {
            Toast.makeText(this, "Bitte, alle Daten eingeben", Toast.LENGTH_SHORT).show();
        } else {
            int bday = convertBDayToInt(birthday.getText().toString());
            User user = new User(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(), email.getText().toString(), password.getText().toString(), bday, genderToSave, this.accType);
            progressBar.setVisibility(View.VISIBLE);
            registerProcess(user);
        }
    }


    private void registerProcess(User user) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);
        Call<Response> call = api.createUser(user);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                    String token = response.headers().get(Const.TOKEN_KEY);
                    saveToken(token);
                    String username = response.headers().get("username");
                    saveUsername(username);
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    goTo("home");
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Username ist schon vorhanden", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println("Error: "+t.toString());
                System.out.println("------------------------------------------------------------------------------");
                Toast.makeText(RegisterActivity.this, "Error: "+t.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                goTo("main");
            }
        });
    }

    public void goTo(String activity) {
        Intent intent;
        if(activity.equalsIgnoreCase("home")){
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        this.finish();
    }

    public void showDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);



        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }

        datePickerDialog.show();
    }

    public final static int convertBDayToInt(String input){
        int dateInt = 0;
        String [] parts = input.split("/");
        dateInt = Integer.parseInt(parts[2]) * 10000 + Integer.parseInt(parts[1]) * 100 + Integer.parseInt(parts[0]);
        return dateInt;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void saveToken(String token){
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.TOKEN_KEY, token);
        editor.commit();
    }

    public void saveUsername(String username){
        sharedPreferences = getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.USERNAME_KEY, username);
        editor.commit();
    }

}
