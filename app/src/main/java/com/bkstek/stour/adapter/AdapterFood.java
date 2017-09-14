package com.bkstek.stour.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bkstek.stour.DetailActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.History;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by thold on 8/26/2017.
 */

public class AdapterFood extends RecyclerView.Adapter<AdapterFood.ViewlHolderFood> {

    private Context context;
    private List<History> historyList;

    public AdapterFood(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public class ViewlHolderFood extends RecyclerView.ViewHolder {
        ImageView imHis;
        TextView txtName;
        RatingBar rbStar;
        TextView txtAddress;

        public ViewlHolderFood(View itemView) {
            super(itemView);

            imHis = (ImageView) itemView.findViewById(R.id.imHis);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            rbStar = (RatingBar) itemView.findViewById(R.id.rbStar);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
        }
    }

    @Override
    public ViewlHolderFood onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_food_layout, parent, false);
        ViewlHolderFood viewlHolderFood = new ViewlHolderFood(view);
        return viewlHolderFood;
    }

    @Override
    public void onBindViewHolder(ViewlHolderFood holder, int position) {
        final History history = historyList.get(position);

        holder.txtName.setText(history.getName());

        Glide.with(context)
                .load(history.getAvatar()).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imHis);

        holder.rbStar.setNumStars(history.getStar());

        holder.txtAddress.setText(history.getAddress());

        holder.imHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDetail = new Intent(context, DetailActivity.class);
                iDetail.putExtra("PlaceID", history.getId());
                iDetail.putExtra("TAG", "FOOD");
                context.startActivity(iDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }


}
