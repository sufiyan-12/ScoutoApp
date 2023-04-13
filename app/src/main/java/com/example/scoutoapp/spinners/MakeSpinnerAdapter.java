package com.example.scoutoapp.spinners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scoutoapp.R;

import java.util.ArrayList;

public class MakeSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;

    public MakeSpinnerAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.make_spinner_layout, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.make_spinner_layout, parent, false);
        TextView textView = view.findViewById(R.id.makeItemText);
        ImageView imageView = view.findViewById(R.id.makeDropdownIcon);

        String item = getItem(position);
        textView.setText(item);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.make_spinner_layout, parent, false);

        TextView textView = view.findViewById(R.id.makeItemText);
        ImageView imageView = view.findViewById(R.id.makeDropdownIcon);

        String item = getItem(position);
        textView.setText(item);
        if(position == 0){
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }

        return view;
    }

    public void updateMakeList(Context context, ArrayList<String> al){
        this.context = context;
        new MakeSpinnerAdapter(context, al);
    }
}

