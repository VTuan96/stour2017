package com.bkstek.stour.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkstek.stour.MapsActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.model.POI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class CustomInfoWindowMapAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public CustomInfoWindowMapAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((MapsActivity)context).getLayoutInflater()
                .inflate(R.layout.layout_custom_item_inforwindow_map, null);

        TextView name = view.findViewById(R.id.txtNameInforWindow);
        TextView address = view.findViewById(R.id.txtAddresInforWindow);
        ImageView image = view.findViewById(R.id.imgItemInfoWindow);

        POI poi = (POI) marker.getTag();

        if (poi != null) {
            name.setText(poi.getName().trim());
            address.setText(poi.getAddress().trim());

            Picasso.with(context).load(Uri.parse(poi.getImage())).centerCrop().resize(70, 70).into(image);

        }
        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
