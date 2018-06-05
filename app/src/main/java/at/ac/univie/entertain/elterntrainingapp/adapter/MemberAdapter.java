package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.R;
import at.ac.univie.entertain.elterntrainingapp.model.User;

public class MemberAdapter extends BaseAdapter {

    List<User> userList;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    Context context;

    public MemberAdapter(Context context, List<User> list) {
        this.context = context;
        this.userList = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.userList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.member_item, parent, false);
        }

        TextView fullName = (TextView) view.findViewById(R.id.member_name_layout_item);
        TextView username = (TextView) view.findViewById(R.id.member_username_layout_item);
        TextView accType = (TextView) view.findViewById(R.id.gender_layout_item);

        User user = (User) getItem(position);

        fullName.setText(user.getFirstName() + " " + user.getLastName());
        username.setText(user.getUsername());
        if (user.getAccType() == 1) {
            accType.setText("Kind");
        }
        if (user.getAccType() == 2) {
            accType.setText("Vater");
        }
        if (user.getAccType() == 3) {
            accType.setText("Mutter");
        }

        return view;
    }
}
