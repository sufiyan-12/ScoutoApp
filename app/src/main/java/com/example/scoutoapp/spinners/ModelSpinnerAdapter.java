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

public class ModelSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;

    public ModelSpinnerAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.model_spinner_layout, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.model_spinner_layout, parent, false);

        TextView textView = view.findViewById(R.id.modelItemText);
        ImageView imageView = view.findViewById(R.id.modelDropdownIcon);

        String item = getItem(position);
        textView.setText(item);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.model_spinner_layout, parent, false);

        TextView textView = view.findViewById(R.id.modelItemText);
        ImageView imageView = view.findViewById(R.id.modelDropdownIcon);

        String item = getItem(position);
        textView.setText(item);
        if(position == 0){
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }

        return view;
    }

    public void updateModelSpinnerList(Context context, ArrayList<String> al){
        this.context = context;
        new ModelSpinnerAdapter(context, al);
    }
}


