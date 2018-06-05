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
import at.ac.univie.entertain.elterntrainingapp.model.Consequence;

public class ConsequenceAdapter extends BaseAdapter {

    private List<Consequence> consList = new ArrayList<Consequence>();
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    Context context;

    public ConsequenceAdapter(Context context, List<Consequence> consequencesList) {
        this.context = context;
        this.consList = consequencesList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return this.consList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.consList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_consequence, parent, false);
        }

        TextView situation = (TextView) view.findViewById(R.id.consequence_item_situation);
        TextView konsequenz = (TextView) view.findViewById(R.id.consequence_item_konsequenz);
        TextView reaktion = (TextView) view.findViewById(R.id.consequence_item_reaktion);

        Consequence con = (Consequence) getItem(position);

        situation.setText(con.getSituation());
        konsequenz.setText(con.getKonsequenz());
        reaktion.setText(con.getReaktion());

        return view;
    }
}
