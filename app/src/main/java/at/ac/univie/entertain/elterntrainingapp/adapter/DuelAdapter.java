package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.R;
import at.ac.univie.entertain.elterntrainingapp.model.quizDuel.Duel;

public class DuelAdapter extends BaseAdapter {


    private List<Duel> duelList = new ArrayList<Duel>();
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    Context context;

    public DuelAdapter(Context context, List<Duel> duelList) {
        this.context = context;
        this.duelList = duelList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return this.duelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_duel, parent, false);
        }

        TextView count = (TextView) view.findViewById(R.id.qd_count);
        TextView status = (TextView) view.findViewById(R.id.qd_status);
        TextView autorScore = (TextView) view.findViewById(R.id.qd_autor_score);
        TextView opponentScore = (TextView) view.findViewById(R.id.qd_gegner_score);

        Duel duel = (Duel) getItem(position);

        count.setText(String.valueOf(position + 1));
        status.setText(duel.getStatus());
        if (duel.isAutorStatus()) {
            autorScore.setText(duel.getScore().getScoreAutor());
        } else {
            autorScore.setText("keine Punkte");
        }
        if (duel.isOpponentStatus()) {
            opponentScore.setText(duel.getScore().getScoreOpponent());
        } else {
            opponentScore.setText("keine Punkte");
        }

        return view;
    }
}
