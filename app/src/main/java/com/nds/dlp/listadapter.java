package com.nds.dlp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class listadapter extends BaseAdapter{

    private Context context;
    private ArrayList<String> list;
    private ArrayList<String> list2;
    private ListItemClickHelp callback;
    private LayoutInflater layoutInflater;


    public listadapter(ArrayList<String> list, ArrayList<String> list2, Context context, ListItemClickHelp callback) {
        this.list = list;
        this.list = list2;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            viewHolder.button1 = (Button) convertView.findViewById(R.id.button21);
            viewHolder.button2 = (Button) convertView.findViewById(R.id.button2);
            viewHolder.button3 = (Button) convertView.findViewById(R.id.button3);
            viewHolder.button4 = (Button) convertView.findViewById(R.id.button4);
            viewHolder.textView = convertView.findViewById(R.id.textView);
            viewHolder.textView2 = convertView.findViewById(R.id.textView2);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(list.get(position));
        viewHolder.textView2.setText(list.get(position));



        final View view = convertView;
        final int p = position;

        final int one = viewHolder.button1.getId();
        viewHolder.button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(view, parent, p, one);
            }
        });

        final int two = viewHolder.button2.getId();
        viewHolder.button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(view, parent, p, two);
            }
        });

        final int three = viewHolder.button3.getId();
        viewHolder.button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(view, parent, p, three);
            }
        });

        final int four = viewHolder.button4.getId();
        viewHolder.button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(view, parent, p, four);
            }
        });

        return convertView;
    }

    public final static class ViewHolder{
        Button button1, button2, button3,button4;
        TextView textView;
        TextView textView2;
    }

}
