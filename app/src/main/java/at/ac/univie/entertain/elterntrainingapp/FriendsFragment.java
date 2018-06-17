package at.ac.univie.entertain.elterntrainingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.adapter.MemberAdapter;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String USER = "user";

    private ListView userListView;
    private MemberAdapter userAdapter;
    private SharedPreferences sharedPreferences;
    View familyView;
    private Button addMemberBtn, detachBtn, updateBtn;
    private List<User> userList;
    private SwipeRefreshLayout swipeRefresh;



    //private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }


    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.familyView = inflater.inflate(R.layout.fragment_friends, container, false);
        addMemberBtn = (Button) familyView.findViewById(R.id.AddMemberBtn);
        detachBtn = (Button) familyView.findViewById(R.id.detachFamilyBtn);
        userListView = (ListView) familyView.findViewById(R.id.memberlist_view);
        updateBtn = (Button) familyView.findViewById(R.id.update_family_memberlist_btn);
        swipeRefresh = (SwipeRefreshLayout) familyView.findViewById(R.id.swipRefreshMembers);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));

        loadMemberList();

        detachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detachFromFamily();
            }
        });

        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddMemberFragment();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMemberList();
            }
        });

        return familyView;
    }

    public void toAddMemberFragment() {
        Fragment addMemberFragment = new AddMemberFragment();
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_home, addMemberFragment).commit();
    }

    public void detachFromFamily() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<Response> call = api.detachFamily(token, username);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadMemberList();
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMemberList() {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<List<User>> call = api.getMembers(token, username);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, retrofit2.Response<List<User>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    userList = response.body();
                    if (userList == null || userList.isEmpty()) {
                        Toast.makeText(getActivity(), "Keine Mitglieder vorhanden!", Toast.LENGTH_SHORT).show();
                    } else if (!userList.isEmpty()) {
                        userAdapter = new MemberAdapter(getActivity(), userList);
                        userListView.setAdapter(userAdapter);
                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                removeMemberDialog(userList.get(position).getUsername());
                                //removeMember(userList.get(position).getUsername());
                            }
                        });
//                        userListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                            @Override
//                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                                return true;
//                            }
//                        });
                    } else {
                        Toast.makeText(getActivity(), "Fehler aufgetreten", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeMemberDialog(String rel) {

        final String relative = rel;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wirklich löschen?");

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeMember(relative);
                        loadMemberList();

                    }
                }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void removeMember(String relative) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();

        Call<Response> call = api.removeMember(token, username, relative);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    //loadMemberList();
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

    @Override
    public void onRefresh() {
        loadMemberList();
    }
}
