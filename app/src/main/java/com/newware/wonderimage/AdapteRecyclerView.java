package com.newware.wonderimage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by Bhuvaneshvar Nath Srivastava on 24-07-2018.
 * Copyright (c) 2018
 **/
public class AdapteRecyclerView extends RecyclerView.Adapter<AdapteRecyclerView.MyViewHolder> {

    private ArrayList<DataList> dataLists;
    private Context context;
    public static final String EXTRA_URL = "imgUrl";
    public static final String EXTRA_CREATOR = "creator";
    public static final String EXTRA_LIKES = "likes";
    public static final String HD_IMAGE = "hdImage";

    public AdapteRecyclerView(ArrayList<DataList> dataLists, Context context) {
        this.dataLists = dataLists;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_items, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DataList dataList = dataLists.get(position);

        holder.tv_creator.setText(dataList.getCreator());
        holder.tv_likes.setText("Likes : " + dataList.getLikes());

        Glide.with(context).load(dataList.getImgUrl()).into(holder.img_to_display);

    }


    @Override
    public int getItemCount() {
        return dataLists.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_to_display;
        public TextView tv_creator, tv_likes;

        public MyViewHolder(View itemView) {
            super(itemView);

            img_to_display = itemView.findViewById(R.id.iv_card_background);
            tv_creator = itemView.findViewById(R.id.tv_creator);
            tv_likes = itemView.findViewById(R.id.tv_likes);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataList clickedItem = dataLists.get(getAdapterPosition());

                    Intent gotoDetailActivity = new Intent(context, ImageDetailActivity.class);
                    gotoDetailActivity.putExtra(EXTRA_URL, clickedItem.getImgUrl());
                    gotoDetailActivity.putExtra(EXTRA_CREATOR, clickedItem.getCreator());
                    gotoDetailActivity.putExtra(EXTRA_LIKES, clickedItem.getLikes());
                    gotoDetailActivity.putExtra(HD_IMAGE,clickedItem.getHdImgLink());
                    gotoDetailActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(gotoDetailActivity);
                }
            });
        }
    }


}

