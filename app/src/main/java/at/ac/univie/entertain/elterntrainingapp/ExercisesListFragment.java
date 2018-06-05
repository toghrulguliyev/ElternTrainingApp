package at.ac.univie.entertain.elterntrainingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ExercisesListFragment extends Fragment {


    View exListView;
    private Button random, selfBtn, goalsBtn, gedankeBtn, lobenBtn, consBtn;

    public ExercisesListFragment() {
        // Required empty public constructor
    }


    public static ExercisesListFragment newInstance(String param1, String param2) {
        ExercisesListFragment fragment = new ExercisesListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

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
        exListView = inflater.inflate(R.layout.fragment_exercises_list, container, false);

        random = (Button) exListView.findViewById(R.id.pieBtn);
        selfBtn = (Button) exListView.findViewById(R.id.selfportrait_btn);
        goalsBtn = (Button) exListView.findViewById(R.id.goals_ex);
        lobenBtn = (Button) exListView.findViewById(R.id.loben_Btn);
        gedankeBtn = (Button) exListView.findViewById(R.id.gedanke_Btn);
        consBtn = (Button) exListView.findViewById(R.id.consequenceBtn);


        gedankeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment dysfunctionalFragment = new DysfunctionalFragment();
                FragmentManager manager = getActivity().getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_home, dysfunctionalFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        lobenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment lobenFragment = new LobenFragment();
                FragmentManager manager = getActivity().getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_home, lobenFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        consBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment consequenceFragment = new ConsequenceFragment();
                FragmentManager manager = getActivity().getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_home, consequenceFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        goalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment goalsFragment = new GoalsFragment();
                FragmentManager manager = getActivity().getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_home, goalsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        selfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selfportraitFragment = new SelfportraitFragment();
                FragmentManager manager = getActivity().getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_home, selfportraitFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment emotionsFragment = new EmotionsFragment();
                FragmentManager manager = getActivity().getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_home, emotionsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



        return exListView;
    }



}
