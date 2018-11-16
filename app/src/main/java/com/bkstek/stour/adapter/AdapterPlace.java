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
import com.bkstek.stour.NoneServiceActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.Place;
import com.bkstek.stour.util.CommonDefine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by acebk on 8/2/2017.
 */

public class AdapterPlace extends RecyclerView.Adapter<AdapterPlace.ViewHolderPlace> {

    private Context context;
    private List<Place> placeList;

    public AdapterPlace(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }


    public class ViewHolderPlace extends RecyclerView.ViewHolder {
        ImageView imgPlace;
        TextView txtPlaceName;

        public ViewHolderPlace(View itemView) {
            super(itemView);

            imgPlace = (ImageView) itemView.findViewById(R.id.imgPlace);
            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);

        }
    }

    @Override
    public ViewHolderPlace onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_place_layout, parent, false);
        ViewHolderPlace viewHolderPlace = new ViewHolderPlace(view);
        return viewHolderPlace;
    }

    @Override
    public void onBindViewHolder(ViewHolderPlace holder, int position) {
        final Place place = placeList.get(position);
        if (place.getName().length() > 40)
            holder.txtPlaceName.setText(place.getName().substring(0,40) + "...");
        else holder.txtPlaceName.setText(place.getName());


        Glide.with(context)
                .load(place.getAvatar())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.imgPlace);

        holder.imgPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iDetail = new Intent(context, NoneServiceActivity.class);
                iDetail.putExtra("PlaceID", place.getId());
                iDetail.putExtra(CommonDefine.TAG, "HOME");
                iDetail.putExtra(CommonDefine.VIDEO_DIR, place.getVideoDir());
                context.startActivity(iDetail);

            }
        });

    }

    @Override
    public int getItemCount() {
        int num = 1;
        if (num * 15 > placeList.size()) {
            return placeList.size();
        } else {
            return num * 15;
        }
    }


}
