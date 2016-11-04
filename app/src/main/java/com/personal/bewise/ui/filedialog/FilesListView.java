package com.personal.bewise.ui.filedialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FilesListView extends ArrayAdapter<FilesData> {

    private LayoutInflater layoutInflater;

    private ArrayList<FilesData> filesList;

    private int resource;

    public FilesListView(Context context, int resource, ArrayList<FilesData> filesList) {
        super(context, resource);
        this.filesList = filesList;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Override
    public FilesData getItem(int position) {
        return filesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh = new ViewHolder();

        if (convertView == null) {
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resource, null);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        FilesData budget = filesList.get(position);

        return convertView;
    }

    static class ViewHolder {
        TextView budget;
        TextView amountAllocated;
        TextView amountUtilized;
    }

}
