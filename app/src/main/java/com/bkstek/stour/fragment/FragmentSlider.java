package com.bkstek.stour.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bkstek.stour.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by acebk on 8/5/2017.
 */

public class FragmentSlider extends Fragment {
    ImageView imSlider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_layout, container, false);
        Bundle bundle = getArguments();
        String linkImage = bundle.getString("linkImage");
        imSlider = (ImageView) view.findViewById(R.id.imSlider);

        Glide.with(getActivity())
                .load(linkImage)
                .into(imSlider);
        return view;
    }
}
