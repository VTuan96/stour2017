package com.bkstek.stour.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bkstek.stour.R;
import com.bkstek.stour.model.POI;

import java.util.List;

/**
 * Created by duy tho le on 10/2/2017.
 */

public class AdapterOrigin extends RecyclerView.Adapter<AdapterOrigin.ViewHolder> {

    private Context context;
    private List<POI> poiList;

    public AdapterOrigin(Context context, List<POI> poiList) {
        this.context = context;
        this.poiList = poiList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imPOI;
        TextView txtName, txtAddress;
        RelativeLayout rlOrigin;

        public ViewHolder(View itemView) {
            super(itemView);
            rlOrigin = (RelativeLayout) itemView.findViewById(R.id.rlOrigin);
            imPOI = (ImageView) itemView.findViewById(R.id.imPOI);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_origin_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        POI poi = poiList.get(position);

        holder.txtName.setText(poi.getName());

        holder.rlOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return poiList.size();
    }


}
