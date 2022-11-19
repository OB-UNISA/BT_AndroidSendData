package com.bilovus.bt_androidsenddata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class URLAdapter extends ArrayAdapter<String> {
    public URLAdapter(Context context, List<String> urls) {
        super(context, 0, urls);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String url = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.url_item, parent, false);
        }
        TextView urlText = convertView.findViewById(R.id.itemURL);
        urlText.setText(url);

        return convertView;
    }

}

