package edu.sjsu.ireportgrp8.fragments;

import android.net.Uri;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.sjsu.ireportgrp8.Interfaces.IimageElementClicked;
import edu.sjsu.ireportgrp8.R;
import edu.sjsu.ireportgrp8.ReportActivity;

/**
 * Created by Shounak on 12/8/2016.
 */

public class ImageElementFragment extends Fragment implements View.OnClickListener{
    private View RootView;
    ImageView iv;
    String id;
    Uri image;

    //@Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.fragment_image_element, container, false);
        id = getArguments().getString("id");
        image = (Uri) getArguments().getParcelable("img");
        iv = (ImageView)RootView.findViewById(R.id.Img_imageElement);
        iv.setImageURI(image);
        iv.setOnClickListener(this);



        return RootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        if (v instanceof ImageView)
        {
            IimageElementClicked inter = (ReportActivity) getActivity();
            inter.closeButtonClicked(id);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
