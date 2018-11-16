package com.bkstek.stour.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bkstek.stour.R;
import com.bkstek.stour.component.DialogDestination;
import com.bkstek.stour.component.DialogPOIOrigin;
import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.CommonDefine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duy tho le on 10/2/2017.
 */

public class AdapterOrigin extends RecyclerView.Adapter<AdapterOrigin.ViewHolder> {

    private Context context;
    private List<POI> poiList;
    private DialogPOIOrigin origin;
    private List<LatLng> latLngList;
    private String TAG ;
    private ArrayList<POI> tempList;

    public AdapterOrigin(Context context, List<POI> poiList, DialogPOIOrigin origin, List<LatLng> latLngList, String TAG) {
        this.context = context;
        this.poiList = poiList;
        this.origin = origin;
        this.latLngList = latLngList;
        this.TAG = TAG;
        tempList = new ArrayList<>();
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final POI poi = poiList.get(position);

        holder.txtName.setText(poi.getName());
        Glide.with(context)
                .load(poi.getImage())
                .apply(new RequestOptions()
                    .centerCrop())
                .into(holder.imPOI);

        holder.rlOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TAG.equals(CommonDefine.ROUTING)) {
                    latLngList.clear();
                    Double lat = poi.getLatitude();
                    Double lon = poi.getLongitude();
                    LatLng latLng = new LatLng(lat, lon);
                    latLngList.add(latLng);
                    tempList.add(poi);
                    origin.dismiss();
                    poiList.remove(position);
                    new DialogDestination(context, poiList, tempList, latLngList).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return poiList.size();
    }


}
