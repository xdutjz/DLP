package com.nds.dlp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class RollItemAdapter extends ArrayAdapter<RollItem> {
    private int reourceId;

    public RollItemAdapter(Context context, int textViewResourceId, List<RollItem> objects){
        super(context, textViewResourceId, objects);
        reourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        RollItem rollItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(reourceId, parent, false);
        ImageView itemImage = (ImageView)view.findViewById(R.id.imageView3);
        TextView appName = (TextView)view.findViewById(R.id.textView31);
        TextView packageName = (TextView)view.findViewById(R.id.textView32);

        itemImage.setImageDrawable(rollItem.getDrawable());
        appName.setText(rollItem.getAppName());
        packageName.setText(rollItem.getPackageName());
        return view;
    }
}
