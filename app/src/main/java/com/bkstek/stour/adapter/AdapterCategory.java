package com.bkstek.stour.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.R;
import com.bkstek.stour.model.Category;
import com.bkstek.stour.model.Place;
import com.bkstek.stour.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_PLACE_BY_CATE;

/**
 * Created by acebk on 8/2/2017.
 */

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolderCate> {

    private Context context;
    private List<Category> categoryList;
    private AdapterPlace adapterPlace;
    private RecyclerView.LayoutManager layoutManager;


    public AdapterCategory(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }


    public class ViewHolderCate extends RecyclerView.ViewHolder {
        TextView txtCateName;
        RecyclerView recyclerPlace;

        public ViewHolderCate(View itemView) {
            super(itemView);

            txtCateName = (TextView) itemView.findViewById(R.id.txtCateName);
            recyclerPlace = (RecyclerView) itemView.findViewById(R.id.recyclerPlace);
        }
    }

    private void GetPlaceByCate(final ViewHolderCate holder, int cateId) {
        String url = GET_PLACE_BY_CATE + "?categoryId=" + cateId;
        final List<Place> placeList = new ArrayList<>();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();

                            for (int i = 0; i < count; i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                Place place = new Place();
                                place.setId(jsonObject.getInt("Id"));
                                place.setName(jsonObject.getString("Name"));
                                place.setAvatar(jsonObject.getString("Avatar"));

                                placeList.add(place);
                            }

                            adapterPlace = new AdapterPlace(context, placeList);
                            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                            holder.recyclerPlace.setLayoutManager(layoutManager);
                            holder.recyclerPlace.setAdapter(adapterPlace);
                            adapterPlace.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.d("error", "get place " + e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "connect get place " + error.toString());
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
    }


    @Override
    public ViewHolderCate onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_cate_layout, parent, false);
        ViewHolderCate viewHolderCate = new ViewHolderCate(view);
        return viewHolderCate;
    }

    @Override
    public void onBindViewHolder(ViewHolderCate holder, int position) {
        Category category = categoryList.get(position);
        holder.txtCateName.setText(category.getName());

        GetPlaceByCate(holder, category.getId());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}
