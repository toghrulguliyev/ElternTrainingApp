package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.R;
import at.ac.univie.entertain.elterntrainingapp.model.Emotions;

public class SimilarityAdapter extends BaseAdapter {

    List<Emotions> emoList;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    Context context;
    float istEmotions;

    public SimilarityAdapter(Context context, List<Emotions> list, float istEmotions) {
        this.context = context;
        this.emoList = list;
        this.istEmotions = istEmotions;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return emoList.size();
    }

    @Override
    public Object getItem(int position) {
        return emoList.get(0);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.progressbar_similarity_item, parent, false);
        }

        TextView data = (TextView) view.findViewById(R.id.piechart_similarity_percentage_view);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.piechart_similaritybar);



        data.setText(emoList.get(position).getAutor() + ": ");

        float memberEmotions = (emoList.get(position).getEmotions()[0] + emoList.get(position).getEmotions()[2] + emoList.get(position).getEmotions()[4] + emoList.get(position).getEmotions()[6])/400;

        if (istEmotions > memberEmotions) {
            bar.setProgress(Math.round(memberEmotions/istEmotions));
        } else {
            bar.setProgress(Math.round(istEmotions/memberEmotions));
        }

        return view;
    }


}
