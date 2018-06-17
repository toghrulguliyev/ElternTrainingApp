package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
        return emoList.get(position);
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
        bar.setMax(100);

        Emotions emo = (Emotions) getItem(position);

        if (emo != null) {
            //data.setText(emo.getAutor() + ": ");

            float memberEmotions = (emo.getEmotions()[0] + emo.getEmotions()[2] + emo.getEmotions()[4] + emo.getEmotions()[6]) / 400;

            System.out.println("memberEmotions = " + memberEmotions * 100);
            System.out.println("istEmotions = " + istEmotions * 100);

            if (istEmotions > memberEmotions) {
                System.out.println(Math.round((((memberEmotions * 100) / (istEmotions * 100))) * 100));
                bar.setProgress(Math.round((((memberEmotions * 100) / (istEmotions * 100))) * 100));
                DecimalFormat df = new DecimalFormat("#.#");
                df.setRoundingMode(RoundingMode.CEILING);
                float sim = Math.round(((memberEmotions * 100) / (istEmotions * 100)) * 100);
                df.format(sim);
                data.setText(emo.getAutor() + " (" + Math.round(((memberEmotions * 100) / (istEmotions * 100)) * 100) + "%)");
            } else {
                System.out.println(Math.round(((istEmotions * 100) / (memberEmotions * 100)) * 100));
                bar.setProgress(Math.round(((istEmotions * 100) / (memberEmotions * 100)) * 100));
                data.setText(emo.getAutor() + " (" + Math.round(((istEmotions * 100) / (memberEmotions * 100)) * 100) + "%)");
            }
        }

        return view;
    }


}
