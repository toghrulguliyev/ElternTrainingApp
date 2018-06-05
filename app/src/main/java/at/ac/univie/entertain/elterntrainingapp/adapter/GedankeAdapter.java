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
import at.ac.univie.entertain.elterntrainingapp.model.Gedanke;

public class GedankeAdapter extends BaseAdapter {

    private SharedPreferences sharedPreferences;
    private List<Gedanke> gedankeList;
    private LayoutInflater layoutInflater;
    Context context;

    public GedankeAdapter(Context context, List<Gedanke> gedankeList) {
        this.context = context;
        this.gedankeList = gedankeList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.gedankeList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.gedankeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_dysfunction, parent, false);
        }

        TextView situation = (TextView) view.findViewById(R.id.dys_situation_item);
        TextView interpret = (TextView) view.findViewById(R.id.dys_interpretation_item);
        TextView feel = (TextView) view.findViewById(R.id.dys_feel_item);
        TextView altInterpret = (TextView) view.findViewById(R.id.dys_alterternative_item);
        TextView dysFeel = (TextView) view.findViewById(R.id.dys_alt_feel_item);

        Gedanke gedanke = getGedanke(position);

        situation.setText(gedanke.getSituation());
        interpret.setText(gedanke.getBewertung());
        feel.setText(gedanke.getFeel());
        altInterpret.setText(gedanke.getAltBewertung());
        dysFeel.setText(gedanke.getAltReaktion());

        return view;
    }

    private Gedanke getGedanke(int position) {
        return (Gedanke) getItem(position);
    }
}
