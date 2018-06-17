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

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.R;
import at.ac.univie.entertain.elterntrainingapp.model.quizDuel.Duel;

import static android.content.Context.MODE_PRIVATE;

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
        return this.duelList.size();
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
        TextView gegner = (TextView) view.findViewById(R.id.qd_gegner_score);

        Duel duel = (Duel) getItem(position);

        count.setText(String.valueOf(position + 1));
        status.setText(duel.getStatus());
//        if (duel.isAutorStatus()) {
//            autorScore.setText(String.valueOf(duel.getScore().getScoreAutor()) + "/10");
//        } else {
//            autorScore.setText("keine Punkte");
//        }
//        if (duel.isOpponentStatus()) {
//            autorScore.setText(String.valueOf(duel.getScore().getScoreOpponent()) + "/10");
//        } else {
//            autorScore.setText("keine Punkte");
//        }
        if (duel.getAutor().equals(getUsername())) {
            gegner.setText(duel.getOpponent());
            if (duel.isAutorStatus()) {
                autorScore.setText(String.valueOf(duel.getScore().getScoreAutor()) + "/10");
            } else {
                autorScore.setText("keine Punkte");
            }
        } else if (duel.getOpponent().equals(getUsername())) {
            gegner.setText(duel.getAutor());
            if (duel.isOpponentStatus()) {
                autorScore.setText(String.valueOf(duel.getScore().getScoreOpponent()) + "/10");
            } else {
                autorScore.setText("keine Punkte");
            }
        }

        return view;
    }

    public String getUsername() {
        sharedPreferences = context.getSharedPreferences(Const.SAVE_FILE,MODE_PRIVATE);
        return sharedPreferences.getString(Const.USERNAME_KEY,"");
    }
}
