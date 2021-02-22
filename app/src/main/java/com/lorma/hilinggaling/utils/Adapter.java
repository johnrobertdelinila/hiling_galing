package com.lorma.hilinggaling.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorma.hilinggaling.R;
import com.lorma.hilinggaling.SearchHerbalActivity;

import java.util.ArrayList;

/**
 * Created by Parsania Hardik on 26-Jun-17.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Herbal> imageHerbalArrayList;
    private ArrayList<Herbal> arraylist;

    public Adapter(Context ctx, ArrayList<Herbal> imageHerbalArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageHerbalArrayList = imageHerbalArrayList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(SearchHerbalActivity.herbalNamesArrayList);
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {
        holder.plantName.setText(imageHerbalArrayList.get(position).getName());
        holder.drawable.setImageResource(imageHerbalArrayList.get(position).getImage_drawable());
        holder.otherName.setText(imageHerbalArrayList.get(position).getScientific());
    }

    @Override
    public int getItemCount() {
        return imageHerbalArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView plantName, otherName;
        private ImageView drawable;
        public MyViewHolder(View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.plant_name);
            drawable = itemView.findViewById(R.id.image);
            otherName = itemView.findViewById(R.id.other_name);
        }
    }
}

