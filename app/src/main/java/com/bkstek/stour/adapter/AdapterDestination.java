package com.bkstek.stour.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bkstek.stour.R;
import com.bkstek.stour.model.POI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by duy tho le on 10/3/2017.
 */

public class AdapterDestination extends RecyclerView.Adapter<AdapterDestination.ViewHolder> {

    private Context context;
    private List<POI> poiList;
    private List<LatLng> latLngList;

    public AdapterDestination(Context context, List<POI> poiList, List<LatLng> latLngList) {
        this.context = context;
        this.poiList = poiList;
        this.latLngList = latLngList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imPOI;
        TextView txtName, txtAddress;
        RelativeLayout rlDes;
        CheckBox cbCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            rlDes = (RelativeLayout) itemView.findViewById(R.id.rlDes);
            imPOI = (ImageView) itemView.findViewById(R.id.imPOI);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            cbCheck = (CheckBox) itemView.findViewById(R.id.cbCheck);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_des_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final POI poi = poiList.get(position);
        //  final Boolean isClick = false;

        holder.txtName.setText(poi.getName());
        Glide.with(context)
                .load(poi.getImage()).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imPOI);


        holder.rlDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                isClick = !isClick;
//                if (isClick) {
                Float lat = Float.parseFloat(poi.getLatitude());
                Float lon = Float.parseFloat(poi.getLongitude());
                final LatLng latLng = new LatLng(lat, lon);
                holder.cbCheck.setChecked(true);
                latLngList.add(latLng);
                // Toast.makeText(context, latLngList.size() + "", Toast.LENGTH_LONG).show();

//                } else {
//                    holder.cbCheck.setChecked(false);
//                    latLngList.remove(latLng);
//                    Toast.makeText(context, latLngList.size() + "", Toast.LENGTH_LONG).show();
//                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return poiList.size();
    }


}
