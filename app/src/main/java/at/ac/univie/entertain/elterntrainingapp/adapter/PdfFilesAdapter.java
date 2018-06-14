package at.ac.univie.entertain.elterntrainingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.R;

public class PdfFilesAdapter extends BaseAdapter{

    List<File> fileList;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    Context context;

    public PdfFilesAdapter(Context context, List<File> list) {
        this.context = context;
        this.fileList = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.pdf_list_item, parent, false);
        }

        TextView count = (TextView) view.findViewById(R.id.pdf_count);
        TextView pdfName = (TextView) view.findViewById(R.id.pdf_name);

        File file = (File) getItem(position);

        count.setText(String.valueOf(position + 1));
        pdfName.setText(file.getName());

        return view;
    }
}
