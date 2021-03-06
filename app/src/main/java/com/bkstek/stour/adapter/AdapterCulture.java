package com.bkstek.stour.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bkstek.stour.DetailActivity;
import com.bkstek.stour.NoneServiceActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.History;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by thold on 8/26/2017.
 */

public class AdapterCulture extends RecyclerView.Adapter<AdapterCulture.ViewHolderCulture> {

    private Context context;
    private List<History> historyList;

    public AdapterCulture(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public class ViewHolderCulture extends RecyclerView.ViewHolder {
        ImageView imHis;
        TextView txtName;
        RatingBar rbStar;
        TextView txtAddress;
        CardView cvContainCulture;

        public ViewHolderCulture(View itemView) {
            super(itemView);

            imHis = (ImageView) itemView.findViewById(R.id.imHis);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
//            rbStar = (RatingBar) itemView.findViewById(R.id.rbStar);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            cvContainCulture = itemView.findViewById(R.id.cvContainCulture);
        }
    }

    @Override
    public ViewHolderCulture onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_culture_layout, parent, false);
        ViewHolderCulture viewHolderCulture = new ViewHolderCulture(view);
        return viewHolderCulture;
    }

    @Override
    public void onBindViewHolder(ViewHolderCulture holder, int position) {
        final History history = historyList.get(position);

        if (history.getName().length() > 20)
            holder.txtName.setText(history.getName().substring(0,20) + "...");
        else holder.txtName.setText(history.getName());


        Glide.with(context)
                .load(history.getAvatar())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.imHis);

//        holder.rbStar.setNumStars(history.getStar());

        if (history.getAddress().length() > 60)
            holder.txtAddress.setText(history.getAddress().substring(0,40) + "...");
        else holder.txtAddress.setText(history.getAddress());

        holder.cvContainCulture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDetail = new Intent(context, NoneServiceActivity.class);
                iDetail.putExtra("PlaceID", history.getId());
                iDetail.putExtra("TAG", "CUL");
                context.startActivity(iDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }


}
