package at.ac.univie.entertain.elterntrainingapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.network.APIInterface;
import at.ac.univie.entertain.elterntrainingapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class AddMemberFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private SharedPreferences sharedPreferences;
    private Button addMemberBtn, abbrechenBtn;
    private ListView searchListView;
    private SearchView searchMember;
    private List<String> usernames;
    View addMemberView;
    private ArrayAdapter<String> adapter;

    public AddMemberFragment() {
        // Required empty public constructor
    }

    public static AddMemberFragment newInstance(String param1, String param2) {
        AddMemberFragment fragment = new AddMemberFragment();
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
        addMemberView = inflater.inflate(R.layout.fragment_add_member, container, false);

        //getDialog().setTitle("Familienmitglieder");
        System.out.println("Add MEMBER GETFAMILYID = " + getFamilyId());

        searchListView = (ListView) addMemberView.findViewById(R.id.search_listview);
        searchMember = (SearchView) addMemberView.findViewById(R.id.search_member);
        addMemberBtn = (Button) addMemberView.findViewById(R.id.search_addMemberBtn);
        abbrechenBtn = (Button) addMemberView.findViewById(R.id.search_abbrechen);

        searchMember.setQueryHint("Username eingeben..");
        searchMember.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
//                if (usernames != null && usernames.size() > 0) {
//                    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, usernames);
//                    searchListView.setAdapter(adapter);
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        abbrechenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFriendsFragment();
            }
        });

        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMember.getQuery().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Bitte ein Username eingeben...", Toast.LENGTH_LONG).show();
                } else {
                    addMember(searchMember.getQuery().toString());
                    toFriendsFragment();
                }
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchMember.setQuery(usernames.get(position), false);
            }
        });

        return addMemberView;
    }

    public void toFriendsFragment() {
        Fragment friendsFragment = new FriendsFragment();
        FragmentManager manager = getActivity().getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_home, friendsFragment).commit();
    }

    public void addMember(String txt) {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();
        String username = getUsername();


        Call<Response> call = api.addRelative(token, username, txt);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    String familyId = response.headers().get(Const.FAMILY_ID);
                    System.out.println("FamilyID = " + familyId);
                    if (getFamilyId() != null && !getFamilyId().isEmpty()) {
                        if (familyId == null || familyId.isEmpty()) {
                            return;
                        } else if (familyId != null && !familyId.isEmpty()) {
                            saveFamilyId(familyId);
                        }
                    } else if (getFamilyId() == null || getFamilyId().isEmpty()) {
                        saveFamilyId(familyId);
                    }
                }
                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Fehler auf dem Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchUsers(String txt) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        APIInterface api = retrofit.create(APIInterface.class);

        String token = getToken();

        Call<List<String>> call = api.searchUsers(token, txt);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                if (response.isSuccessful()) {
                    usernames = new ArrayList<String>();
                    usernames = response.body();
                    if (usernames == null) {
                        usernames.add("Nichts gefunden...");
                        return;
                    }
                    if (usernames.size() > 0) {
                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, usernames);
                        searchListView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
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
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        return sharedPreferences.getString(Const.FAMILY_ID,"");
    }

    public void saveFamilyId(String familyId){
        sharedPreferences = getActivity().getSharedPreferences(Const.SAVE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.FAMILY_ID, familyId);
        editor.commit();
    }

}
