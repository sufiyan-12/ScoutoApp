package com.example.scoutoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DashboardRVAdapter extends RecyclerView.Adapter<DashboardRVAdapter.DashboardViewHolder> {
    private Context context;
    private OnCarItemClicked listener;
    private ArrayList<CarItem> list = new ArrayList<>();

    DashboardRVAdapter(OnCarItemClicked listener) {
        super();
        this.listener = listener;
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.car_item_layout, parent, false);
        DashboardViewHolder viewHolder = new DashboardViewHolder(view);
        Button addImageBtn = view.findViewById(R.id.idAddCarImageBtn);
        Button deleteBtn = view.findViewById(R.id.idDeleteCarBtn);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "add image clicked!!", Toast.LENGTH_SHORT).show();
                listener.onImageSet(list.get(viewHolder.getAdapterPosition()));
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Car Item deleted successfully!!", Toast.LENGTH_SHORT).show();
                listener.onItemDelete(list.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DashboardViewHolder holder, int position) {
        CarItem currentItem = list.get(position);
        holder.carMakeTV.setText(currentItem.getCar_make());
        holder.carModelTV.setText(currentItem.getCar_model());
        String imageUrl = currentItem.getImageUrl();
        byte[] stringBytes = currentItem.getImageBytes();
        if(imageUrl != null && !imageUrl.isEmpty()){
            holder.imageIV.setBackground(null);
            if(imageUrl.isEmpty() || imageUrl == null){
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(stringBytes, 0, stringBytes.length);
//                Log.d("dashboardActivity", bitmap.toString());
                holder.imageIV.setImageBitmap(imageBitmap);
            }else{
                holder.imageIV.setImageURI(Uri.parse(imageUrl));
                Log.d("myAdapter", imageUrl);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<CarItem> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public static class DashboardViewHolder extends RecyclerView.ViewHolder {
        public DashboardViewHolder(View itemView) {
            super(itemView);
        }

        ImageView imageIV = itemView.findViewById(R.id.idImageIV);
        TextView carMakeTV = itemView.findViewById(R.id.idCarMakeTV);
        TextView carModelTV = itemView.findViewById(R.id.idCarModelTV);
        Button addImageBtn = itemView.findViewById(R.id.idAddCarImageBtn);
        Button deleteBtn = itemView.findViewById(R.id.idDeleteCarBtn);
    }

}

interface OnCarItemClicked {
    void onItemClicked(CarItem item);
    void onImageSet(CarItem item);
    void onItemDelete(CarItem item, int position);
}
