package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.R;
import at.ac.univie.entertain.elterntrainingapp.model.FamilyRule;

public class FamilyRuleAdapter extends BaseAdapter {

    private SharedPreferences sharedPreferences;
    private List<FamilyRule> ruleList;
    private LayoutInflater layoutInflater;
    Context context;

    public FamilyRuleAdapter(Context context, List<FamilyRule> list) {
        this.context = context;
        this.ruleList = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.ruleList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.ruleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_family_rule_forlist, parent, false);
        }

        TextView ruleCount = (TextView) view.findViewById(R.id.rule_count_item);
        TextView ruleNameTextView = (TextView) view.findViewById(R.id.rule_name_item);
        //TextView ruleTextView = (TextView) view.findViewById(R.id.rule);
        //TextView reasonTextView = (TextView) view.findViewById(R.id.reason);
        //TextView forWhoTextView = (TextView) view.findViewById(R.id.for_who);


        FamilyRule fr = getFamilyRule(position);
        ruleNameTextView.setText(fr.getRuleName());
        ruleCount.setText(String.valueOf(position+1));
        //ruleTextView.setText(fr.getRule());
        //reasonTextView.setText(fr.getReason());
        //forWhoTextView.setText(fr.getForWho());

        return view;
    }

    private FamilyRule getFamilyRule(int position) {
        return (FamilyRule) getItem(position);
    }

}
