package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.R;
import at.ac.univie.entertain.elterntrainingapp.model.Loben;

public class LobenAdapter extends BaseAdapter {

    private SharedPreferences sharedPreferences;
    private List<Loben> lobenList = new ArrayList<Loben>();
    private LayoutInflater layoutInflater;
    Context context;

    public LobenAdapter(Context context, List<Loben> lobenList) {
        this.context = context;
        this.lobenList = lobenList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.lobenList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lobenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_loben, parent, false);
        }

        TextView situation = (TextView) view.findViewById(R.id.gelobt_situation);
        TextView art = (TextView) view.findViewById(R.id.gelobt_art);
        TextView reaktion = (TextView) view.findViewById(R.id.gelobt_reagiert);
        TextView count = (TextView) view.findViewById(R.id.loben_count);

        Loben loben = (Loben) getItem(position);

        situation.setText(loben.getSituation());
        art.setText(loben.getArt());
        reaktion.setText(loben.getReaktion());
        count.setText(String.valueOf(position + 1));

        return view;
    }
}
