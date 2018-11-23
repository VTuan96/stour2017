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
import android.widget.Toast;

import com.bkstek.stour.DetailActivity;
import com.bkstek.stour.NoneServiceActivity;
import com.bkstek.stour.NotifyActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.History;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by acebk on 8/19/2017.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolderHis> {

    private Context context;
    private List<History> historyList;

    public AdapterHistory(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public class ViewHolderHis extends RecyclerView.ViewHolder {
        ImageView imHis;
        TextView txtName;
        RatingBar rbStar;
        TextView txtAddress;
        CardView cvContain;

        public ViewHolderHis(View itemView) {
            super(itemView);

            imHis = (ImageView) itemView.findViewById(R.id.imHis);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
//            rbStar = (RatingBar) itemView.findViewById(R.id.rbStar);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            cvContain = itemView.findViewById(R.id.cvContain);
        }
    }

    @Override
    public AdapterHistory.ViewHolderHis onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_history_layout, parent, false);
        ViewHolderHis viewHolderHis = new ViewHolderHis(view);
        return viewHolderHis;
    }

    @Override
    public void onBindViewHolder(AdapterHistory.ViewHolderHis holder, int position) {
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

        holder.cvContain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDetail = new Intent(context, NoneServiceActivity.class);
                iDetail.putExtra("PlaceID", history.getId());
                iDetail.putExtra("TAG", "HIS");
                context.startActivity(iDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
