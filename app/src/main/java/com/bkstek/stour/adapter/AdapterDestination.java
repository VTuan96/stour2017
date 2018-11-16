package com.bkstek.stour.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by duy tho le on 10/3/2017.
 */

public class AdapterDestination extends ArrayAdapter<POI> {

    private Context context;
    private List<POI> poiList;
    private List<POI> tempList;
    private List<LatLng> latLngList;

    private boolean[] checkBoxState = null;
    private HashMap<POI, Boolean> checkedForCountry = new HashMap<>();

    public AdapterDestination(@NonNull Context context,   List<POI> poiList,  List<POI> tempList,List<LatLng> latLngList) {
        super(context, R.layout.adapter_des_layout, poiList);
        this.context = context;
        this.poiList = poiList;
        this.latLngList = latLngList;
        this.tempList = tempList;
    }

//    public AdapterDestination(Context context, List<POI> poiList, List<LatLng> latLngList) {
//        this.context = context;
//        this.poiList = poiList;
//        this.latLngList = latLngList;
//    }



    @NonNull
    @Override
    public View getView(final int position,@Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_des_layout, parent, false);
        }
        viewHolder = new ViewHolder(convertView);
        checkBoxState = new boolean[poiList.size()];

        final POI poi = poiList.get(position);
        viewHolder.txtName.setText(poi.getName());
        Glide.with(context)
                .load(poi.getImage())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(viewHolder.imPOI);


        if(checkBoxState != null)
            viewHolder.cbCheck.setChecked(checkBoxState[position]);

        viewHolder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    checkBoxState[position] = true;
                    ischecked(position,true);
                    Double lat = poi.getLatitude();
                    Double lon = poi.getLongitude();
                    final LatLng latLng = new LatLng(lat, lon);
                    latLngList.add(latLng);
                    tempList.add(poi);
                }
                else {
                    Double lat = poi.getLatitude();
                    Double lon = poi.getLongitude();
                    checkBoxState[position] = false;
                    ischecked(position,false);
                    LatLng latLng = new LatLng(lat, lon);
                    latLngList.remove(latLng);
                    tempList.remove(poi);
                }
            }
        });


        /**if country is in checkedForCountry then set the checkBox to true **/
        if (checkedForCountry.get(poi) != null) {
            viewHolder.cbCheck.setChecked(checkedForCountry.get(poi));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return poiList.size();
    }

    public void ischecked(int position,boolean flag )
    {
        checkedForCountry.put(this.poiList.get(position), flag);
    }

    public class ViewHolder  {
        ImageView imPOI;
        TextView txtName, txtAddress;
        RelativeLayout rlDes;
        CheckBox cbCheck;

        public ViewHolder(View itemView) {
            super();
            rlDes = (RelativeLayout) itemView.findViewById(R.id.rlDes);
            imPOI = (ImageView) itemView.findViewById(R.id.imPOI);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            cbCheck = (CheckBox) itemView.findViewById(R.id.cbCheck);
        }
    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.adapter_des_layout, parent, false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        final POI poi = poiList.get(position);
//        //  final Boolean isClick = false;
//        final int pos = position;
//
//        holder.txtName.setText(poi.getName());
//        Glide.with(context)
//                .load(poi.getImage())
//                .apply(new RequestOptions()
//                        .centerCrop())
//                .into(holder.imPOI);
//
//        holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    Toast.makeText(context, "Position " + pos, Toast.LENGTH_LONG).show();
//                    Float lat = Float.parseFloat(poi.getLatitude());
//                    Float lon = Float.parseFloat(poi.getLongitude());
//                    final LatLng latLng = new LatLng(lat, lon);
////                    holder.cbCheck.setChecked(true);
//                    latLngList.add(latLng);
//                }
//            }
//        });
//
////        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////
//////                isClick = !isClick;
//////                if (isClick) {
////
////                // Toast.makeText(context, latLngList.size() + "", Toast.LENGTH_LONG).show();
////
//////                } else {
//////                    holder.cbCheck.setChecked(false);
//////                    latLngList.remove(latLng);
//////                    Toast.makeText(context, latLngList.size() + "", Toast.LENGTH_LONG).show();
//////                }
////            }
////        });
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return poiList.size();
//    }


}
