package com.newware.wonderimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 05-08-2018.
 * Copyright (c) 2018
 **/
public class SliderAdapter extends PagerAdapter
{
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context)
    {
        this.context = context;

    }

    public int[] slideimages = {R.drawable.img5,R.drawable.img6,R.drawable.img7};

    public String[] slide_headings = {"Welcome To Wonder-Gallery","See Other User's Photography","All In Real Time"};

    public String[] slide_description = {"In this application You can explore variety of images.Also you can upload your own creativity online so the other can see it.\nAll Library images are property of Pixabay.",
            "Uploaded user's Images is synced with Google's Firebase Database\nDon't worry data is safe and available anytime anywhere.\nAll the users of Wonder-Gallery app can see and download uploaded images on this platform"
            , "Data you upload are send to firebase server and are saved there.Any changes in data reflect to all the user of App."};

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide,container,false);


        TextView tv_heading = view.findViewById(R.id.tv_welcomeNode);
        TextView tv_description  = view.findViewById(R.id.tv_detaild_abot_app);
        ImageView iv_imgIcon = view.findViewById(R.id.iv_slideimg);

        iv_imgIcon.setImageResource(slideimages[position]);
        tv_heading.setText(slide_headings[position]);
        tv_description.setText(slide_description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((RelativeLayout)object);
    }
}
