package com.bkstek.stour.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkstek.stour.DetailActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.History;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by acebk on 9/4/2017.
 */

public class AdapterRestaurant extends RecyclerView.Adapter<AdapterRestaurant.ViewHolderRes> {

    private Context context;
    private List<History> historyList;

    public AdapterRestaurant(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }


    public class ViewHolderRes extends RecyclerView.ViewHolder {
        ImageView imgRes;
        TextView txtResName;

        public ViewHolderRes(View itemView) {
            super(itemView);

            imgRes = (ImageView) itemView.findViewById(R.id.imgRes);
            txtResName = (TextView) itemView.findViewById(R.id.txtResName);
        }
    }


    @Override
    public ViewHolderRes onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_restaurant_layout, parent, false);
        ViewHolderRes viewHolderRes = new ViewHolderRes(view);
        return viewHolderRes;
    }

    @Override
    public void onBindViewHolder(ViewHolderRes holder, int position) {
        final History res = historyList.get(position);

        holder.txtResName.setText(res.getName());

        Glide.with(context)
                .load(res.getAvatar()).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgRes);

        holder.imgRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iDetail = new Intent(context, DetailActivity.class);
                iDetail.putExtra("PlaceID", res.getId());
                iDetail.putExtra("TAG", "RES");
                context.startActivity(iDetail);

            }
        });
    }

    @Override
    public int getItemCount() {
        int num = 1;
        if (num * 15 > historyList.size()) {
            return historyList.size();
        } else {
            return num * 15;
        }
    }


}
