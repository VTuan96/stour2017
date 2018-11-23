package com.bkstek.stour.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bkstek.stour.MapsActivity;
import com.bkstek.stour.NoneServiceActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.History;
import com.bkstek.stour.model.LBS;
import com.bkstek.stour.util.CommonDefine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AdapterLBS extends RecyclerView.Adapter<AdapterLBS.ViewHolderHis>{
    private Context context;
    private List<LBS> lbsList;

    public AdapterLBS(Context context, List<LBS> lbsList) {
        this.context = context;
        this.lbsList = lbsList;
    }

    public class ViewHolderHis extends RecyclerView.ViewHolder {
        TextView txtNameLBS;
        TextView txtAddressLBS;
        TextView txtTelLBS;
        TextView txtOpenLBS;

        LinearLayout lnOpenTimeLBS, lnTelLBS;
        CardView cvContainLBS;

        public ViewHolderHis(View itemView) {
            super(itemView);

            txtAddressLBS = itemView.findViewById(R.id.txtAddressLBS);
            txtNameLBS = itemView.findViewById(R.id.txtNameLBS);
            txtTelLBS = itemView.findViewById(R.id.txtTelLBS);
            txtOpenLBS = itemView.findViewById(R.id.txtOpenLBS);

            lnOpenTimeLBS = itemView.findViewById(R.id.lnOpenTimeLBS);
            lnTelLBS = itemView.findViewById(R.id.lnTelLBS);
            cvContainLBS = itemView.findViewById(R.id.cvContainLBS);
        }
    }

    @Override
    public AdapterLBS.ViewHolderHis onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_item_lbs, parent, false);
        AdapterLBS.ViewHolderHis viewHolderHis = new AdapterLBS.ViewHolderHis(view);
        return viewHolderHis;
    }

    @Override
    public void onBindViewHolder(AdapterLBS.ViewHolderHis holder, int position) {
        final LBS lbs = lbsList.get(position);

        holder.txtNameLBS.setText(lbs.getName().trim());
        holder.txtAddressLBS.setText(lbs.getAddress().trim());
        if (lbs.getTel().trim().length() > 0)
            holder.txtTelLBS.setText(lbs.getTel().trim());
        else
            holder.lnTelLBS.setVisibility(View.GONE);

        if (lbs.getOpen_time().trim().length() > 0)
            holder.txtOpenLBS.setText(lbs.getOpen_time().trim());
        else
            holder.lnOpenTimeLBS.setVisibility(View.GONE);

        holder.cvContainLBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra(CommonDefine.FUNC, CommonDefine.LBS);
                intent.putExtra("name", lbs.getName());
                intent.putExtra("lat", lbs.getLat());
                intent.putExtra("lon", lbs.getLon());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lbsList.size();
    }
}
