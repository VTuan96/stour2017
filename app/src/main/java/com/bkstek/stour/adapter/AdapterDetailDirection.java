package com.bkstek.stour.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bkstek.stour.R;
import com.bkstek.stour.model.DetailDirection;

import java.util.ArrayList;
import java.util.List;

public class AdapterDetailDirection extends ArrayAdapter<DetailDirection> {
    private Context context;
    private ArrayList<DetailDirection> list;

    public AdapterDetailDirection(@NonNull Context context, ArrayList<DetailDirection> list) {
        super(context, R.layout.layout_detail_direction_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent) {
        DetailDirection detailDirection = list.get(position);

        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_detail_direction_item, parent, false);
        }

        DetailHoder holder = new DetailHoder(view);
        holder.txtDetailHolder.setText(Html.fromHtml(detailDirection.getInstruction().trim()));
        holder.txtDistanceHolder.setText(detailDirection.getDistance());

        return view;
    }

    public class DetailHoder{
        private TextView txtDetailHolder;
        private TextView txtDistanceHolder;
        private View view;

        public DetailHoder(View view) {
            this.view = view;
            txtDetailHolder = view.findViewById(R.id.txtDetailHoder);
            txtDistanceHolder = view.findViewById(R.id.txtDistanceHolder);
        }
    }
}
