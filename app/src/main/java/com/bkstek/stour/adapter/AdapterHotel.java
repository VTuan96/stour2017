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
import com.bkstek.stour.ServiceActivity;
import com.bkstek.stour.model.History;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by acebk on 9/4/2017.
 */

public class AdapterHotel extends RecyclerView.Adapter<AdapterHotel.ViewHolderHotel> {

    private Context context;
    private List<History> historyList;

    public AdapterHotel(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }


    public class ViewHolderHotel extends RecyclerView.ViewHolder {
        ImageView imgHotel;
        TextView txtHotelName;

        public ViewHolderHotel(View itemView) {
            super(itemView);

            imgHotel = (ImageView) itemView.findViewById(R.id.imgHotel);
            txtHotelName = (TextView) itemView.findViewById(R.id.txtHotelName);
        }
    }

    @Override
    public ViewHolderHotel onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_hotel_layout, parent, false);
        ViewHolderHotel viewHolderHotel = new ViewHolderHotel(view);
        return viewHolderHotel;
    }

    @Override
    public void onBindViewHolder(ViewHolderHotel holder, int position) {
        final History hotel = historyList.get(position);

        holder.txtHotelName.setText(hotel.getName());

        Glide.with(context)
                .load(hotel.getAvatar())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.imgHotel);

        holder.imgHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iDetail = new Intent(context, ServiceActivity.class);
                iDetail.putExtra("PlaceID", hotel.getId());
                iDetail.putExtra("TAG", "HOTEL");
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
