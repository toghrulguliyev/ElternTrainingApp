package at.ac.univie.entertain.elterntrainingapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Goals;
import at.ac.univie.entertain.elterntrainingapp.model.MyGoals;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class GoalsFragment extends Fragment {

    private Goals goals;
    private MyGoals myGoals, newGoals;
    View goalView;
    private SharedPreferences sharedPreferences;
    private List<String> randomGoals, realGoals, unrealGoals;
    private Button checkBtn, resetBtn, goBackBtn, oldGoalsBtn;

    private TextView goal1, goal2, goal3, goal4, goal5, goal6, real, unreal;
    private TextView ziel1, ziel2, ziel3, ziel4, ziel5, ziel6, check1, check2, check3, check4, check5, check6;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        goalView = inflater.inflate(R.layout.fragment_goals, container, false);

        realGoals = new ArrayList<String>();
        unrealGoals = new ArrayList<String>();


        goal1 = (TextView) goalView.findViewById(R.id.goal1);
        goal2 = (TextView) goalView.findViewById(R.id.goal2);
        goal3 = (TextView) goalView.findViewById(R.id.goal3);
        goal4 = (TextView) goalView.findViewById(R.id.goal4);
        goal5 = (TextView) goalView.findViewById(R.id.goal5);
        goal6 = (TextView) goalView.findViewById(R.id.goal6);

        real = (TextView) goalView.findViewById(R.id.real_goal);
        unreal = (TextView) goalView.findViewById(R.id.unreal_goal);

        checkBtn = (Button) goalView.findViewById(R.id.goals_checkBtn);
        resetBtn = (Button) goalView.findViewById(R.id.goals_resetBtn);
        goBackBtn = (Button) goalView.findViewById(R.id.goals_backBtn);
        oldGoalsBtn = (Button) goalView.findViewById(R.id.goals_oldGoals_checkBtn);

        getGoals();

        getMyGoals();

        goal1.setOnTouchListener(new GoalTouchListener());
        goal2.setOnTouchListener(new GoalTouchListener());
        goal3.setOnTouchListener(new GoalTouchListener());
        goal4.setOnTouchListener(new GoalTouchListener());
        goal5.setOnTouchListener(new GoalTouchListener());
        goal6.setOnTouchListener(new GoalTouchListener());

        real.setOnDragListener(new RealGoalDragListener());
        unreal.setOnDragListener(new UnrealGoalDragListener());

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDrags();
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGoals();
            }
        });

        oldGoalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOldCheck();
            }
        });

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return goalView;
    }



    private final class GoalTouchListener implements OnTouchListener{
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
                 * - clip data could be set to pass data as part of drag
                 * - shadow can be tailored
                 */
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                //start dragging the item touched
                v.startDrag(data, shadowBuilder, v, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private class UnrealGoalDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    System.out.println("ACTION_DROP");
                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextView dropped = (TextView) view;
                    System.out.println("-----------UNREAL SIZE: " + unrealGoals.size() + " ---------");
                    if (unrealGoals.size() < 3) {
                        if (!dropped.getText().toString().isEmpty()) {
                            unrealGoals.add(dropped.getText().toString());
                            System.out.println("----- " + dropped.getText().toString() + " --------");
                        }

                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE);
                        //update the text in the target view to reflect the data being dropped
                        //dropTarget.setText(dropTarget.getText().toString() + dropped.getText().toString());
                        //make it bold to highlight the fact that an item has been dropped
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                        //if an item has already been dropped here, there will be a tag
                        //Object tag = dropTarget.getTag();
                        //if there is already an item here, set it back visible in its original place
//                        if(tag!=null)
//                        {
//                            //the tag is the view id already dropped here
//                            int existingID = (Integer)tag;
//                            //set the original view visible again
//                            goalView.findViewById(existingID).setVisibility(View.VISIBLE);
//                        }
                        //set the tag in the target view being dropped on - to the ID of the view being dropped
                        dropTarget.setTag(dropped.getId());
                        break;
                    } else {
                        //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
                        dropTarget.setOnDragListener(null);
                        //displays message if first character of dropTarget is not equal to first character of dropped
                        Toast.makeText(getActivity(), "Unrealistische Ziele sind schon gefüllt", Toast.LENGTH_LONG).show();
                        break;
                    }
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private class RealGoalDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:

                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextView dropped = (TextView) view;
                    System.out.println("-----------REAL SIZE: " + realGoals.size() + " ---------");
                    if (realGoals.size() < 3) {
                        if (!dropped.getText().toString().isEmpty()) {
                            realGoals.add(dropped.getText().toString());
                            System.out.println("----- " + dropped.getText().toString() + " --------");
                        }

                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE);
                        //update the text in the target view to reflect the data being dropped
                        //dropTarget.setText(dropTarget.getText().toString() + dropped.getText().toString());
                        //make it bold to highlight the fact that an item has been dropped
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                        //if an item has already been dropped here, there will be a tag
                        //Object tag = dropTarget.getTag();
                        //if there is already an item here, set it back visible in its original place
//                        if(tag!=null)
//                        {
//                            //the tag is the view id already dropped here
//                            int existingID = (Integer)tag;
//                            //set the original view visible again
//                            goalView.findViewById(existingID).setVisibility(View.VISIBLE);
//                        }
                        //set the tag in the target view being dropped on - to the ID of the view being dropped
                        dropTarget.setTag(dropped.getId());
                        break;
                    } else {
                        //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
                        dropTarget.setOnDragListener(null);
                        //displays message if first character of dropTarget is not equal to first character of dropped
                        Toast.makeText(getActivity(), "Realistische Ziele sind schon gefüllt", Toast.LENGTH_LONG).show();
                        break;
                    }
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    public void resetDrags() {

        goal1.setVisibility(TextView.VISIBLE);
        goal2.setVisibility(TextView.VISIBLE);
        goal3.setVisibility(TextView.VISIBLE);
        goal4.setVisibility(TextView.VISIBLE);
        goal5.setVisibility(TextView.VISIBLE);
        goal6.setVisibility(TextView.VISIBLE);

        real.setTag(null);
        unreal.setTag(null);

        real.setTypeface(Typeface.DEFAULT);
        unreal.setTypeface(Typeface.DEFAULT);

        real.setOnDragListener(new RealGoalDragListener());

    }

    public void getMyGoals() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<MyGoals> call = api.getMyGoals(token, username);

        call.enqueue(new Callback<MyGoals>() {
            @Override
            public void onResponse(Call<MyGoals> call, retrofit2.Response<MyGoals> response) {
                if (response.isSuccessful()) {
                    myGoals = response.body();
                } else {
                    Toast.makeText(getActivity(), "Du hast keine angegebene Ziele", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyGoals> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getGoals() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Goals> call = api.getAllGoals(token);

        call.enqueue(new Callback<Goals>() {
            @Override
            public void onResponse(Call<Goals> call, retrofit2.Response<Goals> response) {
                if (response.isSuccessful()) {
                    goals = response.body();
                    if (goals != null && goals.getRealGoals().size() > 3 && goals.getUnrealGoals().size() > 3) {
                        setRandomGoals();
                    }
                } else {
                    Toast.makeText(getActivity(), "Keine Ziele gefunden", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Goals> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRandomGoals() {

        randomGoals = new ArrayList<String>();

        while (randomGoals.size() < 3) {
            int random1 = new Random().nextInt(goals.getRealGoals().size());
            int random2 = new Random().nextInt(goals.getRealGoals().size());
            int random3 = new Random().nextInt(goals.getRealGoals().size());
//            System.out.println("REAL");
//            System.out.println("------------ random1 = " + random1);
//            System.out.println("------------ random2 = " + random2);
//            System.out.println("------------ random3 = " + random3);
            if (random1 != random2 && random1 != random3 && random2 != random3) {
                randomGoals.add(goals.getRealGoals().get(random1));
                randomGoals.add(goals.getRealGoals().get(random2));
                randomGoals.add(goals.getRealGoals().get(random3));
            } else {
                System.out.println("random1 = " + random1);
                System.out.println("random1 = " + random2);
                System.out.println("random1 = " + random3);
            }
        }

        while (randomGoals.size() < 6) {
            int random1 = new Random().nextInt(goals.getUnrealGoals().size());
            int random2 = new Random().nextInt(goals.getUnrealGoals().size());
            int random3 = new Random().nextInt(goals.getUnrealGoals().size());
//            System.out.println("UNREAL");
//            System.out.println("-------- random1 = " + random1);
//            System.out.println("-------- random1 = " + random2);
//            System.out.println("-------- random1 = " + random3);

            if (random1 != random2 && random1 != random3 && random2 != random3) {
                randomGoals.add(goals.getUnrealGoals().get(random1));
                randomGoals.add(goals.getUnrealGoals().get(random2));
                randomGoals.add(goals.getUnrealGoals().get(random3));
            } else {
//                System.out.println("random1 = " + random1);
//                System.out.println("random1 = " + random2);
//                System.out.println("random1 = " + random3);
            }
        }

        Collections.shuffle(randomGoals);

        goal1.setText(randomGoals.get(0));
        goal2.setText(randomGoals.get(1));
        goal3.setText(randomGoals.get(2));
        goal4.setText(randomGoals.get(3));
        goal5.setText(randomGoals.get(4));
        goal6.setText(randomGoals.get(5));

    }

    public void showOldCheck() {
        if (myGoals == null || myGoals.getGoals() == null || myGoals.getGoals().size() < 6) {
            Toast.makeText(getActivity(), "Keine Daten vorhanden", Toast.LENGTH_LONG).show();
        } else {
            getMyGoals();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View dialogGoals = getActivity().getLayoutInflater().inflate(R.layout.dialog_goals_check, null);
            builder.setView(dialogGoals);

            ziel1 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel1);
            ziel2 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel2);
            ziel3 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel3);
            ziel4 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel4);
            ziel5 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel5);
            ziel6 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel6);

            check1 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check1);
            check2 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check2);
            check3 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check3);
            check4 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check4);
            check5 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check5);
            check6 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check6);

            ziel1.setText(myGoals.getGoals().get(0));
            ziel2.setText(myGoals.getGoals().get(1));
            ziel3.setText(myGoals.getGoals().get(2));
            ziel4.setText(myGoals.getGoals().get(3));
            ziel5.setText(myGoals.getGoals().get(4));
            ziel6.setText(myGoals.getGoals().get(5));

            check1.setText("Falsch");
            check1.setTextColor(Color.RED);
            check2.setText("Falsch");
            check2.setTextColor(Color.RED);
            check3.setText("Falsch");
            check3.setTextColor(Color.RED);
            check4.setText("Falsch");
            check4.setTextColor(Color.RED);
            check5.setText("Falsch");
            check5.setTextColor(Color.RED);
            check6.setText("Falsch");
            check6.setTextColor(Color.RED);

            if (goals != null) {
                for (String goal : goals.getRealGoals()) {
                    if (goal.equals(ziel1.getText().toString())) {
                        check1.setText("Richtig");
                        check1.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel2.getText().toString())) {
                        check2.setText("Richtig");
                        check2.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel3.getText().toString())) {
                        check3.setText("Richtig");
                        check3.setTextColor(Color.GREEN);
                    }
                }
                for (String goal : goals.getUnrealGoals()) {
                    if (goal.equals(ziel4.getText().toString())) {
                        check4.setText("Richtig");
                        check4.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel5.getText().toString())) {
                        check5.setText("Richtig");
                        check5.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel6.getText().toString())) {
                        check6.setText("Richtig");
                        check6.setTextColor(Color.GREEN);
                    }
                }
            }
            builder.setTitle("Ziele prüfen");
            builder.setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            //builder.create();
            builder.show();
        }
    }

    public void checkGoals() {
        if (realGoals.size() < 3 || unrealGoals.size() < 3) {
            Toast.makeText(getActivity(), "Bitte alle Ziele einsetzen!", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View dialogGoals = getActivity().getLayoutInflater().inflate(R.layout.dialog_goals_check, null);
            builder.setView(dialogGoals);

            ziel1 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel1);
            ziel2 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel2);
            ziel3 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel3);
            ziel4 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel4);
            ziel5 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel5);
            ziel6 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_ziel6);

            check1 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check1);
            check2 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check2);
            check3 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check3);
            check4 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check4);
            check5 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check5);
            check6 = (TextView) dialogGoals.findViewById(R.id.goals_dialog_check6);

            ziel1.setText(realGoals.get(0));
            ziel2.setText(realGoals.get(1));
            ziel3.setText(realGoals.get(2));
            ziel4.setText(unrealGoals.get(0));
            ziel5.setText(unrealGoals.get(1));
            ziel6.setText(unrealGoals.get(2));

            check1.setText("Falsch");
            check1.setTextColor(Color.RED);
            check2.setText("Falsch");
            check2.setTextColor(Color.RED);
            check3.setText("Falsch");
            check3.setTextColor(Color.RED);
            check4.setText("Falsch");
            check4.setTextColor(Color.RED);
            check5.setText("Falsch");
            check5.setTextColor(Color.RED);
            check6.setText("Falsch");
            check6.setTextColor(Color.RED);

            if (goals != null) {
                for (String goal : goals.getRealGoals()) {
                    if (goal.equals(ziel1.getText().toString())) {
                        check1.setText("Richtig");
                        check1.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel2.getText().toString())) {
                        check2.setText("Richtig");
                        check2.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel3.getText().toString())) {
                        check3.setText("Richtig");
                        check3.setTextColor(Color.GREEN);
                    }
                }
                for (String goal : goals.getUnrealGoals()) {
                    if (goal.equals(ziel4.getText().toString())) {
                        check4.setText("Richtig");
                        check4.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel5.getText().toString())) {
                        check5.setText("Richtig");
                        check5.setTextColor(Color.GREEN);
                    }
                    if (goal.equals(ziel6.getText().toString())) {
                        check6.setText("Richtig");
                        check6.setTextColor(Color.GREEN);
                    }
                }
            }
            builder.setTitle("Ziele prüfen");
            builder.setCancelable(false)
                    .setPositiveButton("Speichern",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    List<String> goalsList = new ArrayList<String>();
                                    goalsList.add(ziel1.getText().toString());
                                    goalsList.add(ziel2.getText().toString());
                                    goalsList.add(ziel3.getText().toString());
                                    goalsList.add(ziel4.getText().toString());
                                    goalsList.add(ziel5.getText().toString());
                                    goalsList.add(ziel6.getText().toString());
                                    if (myGoals == null) {
                                        myGoals = new MyGoals(goalsList, getUsername());
                                        saveMyGoals();
                                    } else {
                                        myGoals.setGoals(goalsList);
                                        myGoals.setAutor(getUsername());
                                        saveMyGoals();
                                    }
                                }
                            }).setNegativeButton("Abbrechen",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    public void saveMyGoals() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<Response> call = api.saveMyGoals(token, myGoals);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
