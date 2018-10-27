package com.newware.wonderimage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
 * Created by Bhuvaneshvar Nath Srivastava on 29-07-2018.
 * Copyright (c) 2018
 **/
public class CurrentUserImgShowAdapter extends RecyclerView.Adapter<CurrentUserImgShowAdapter.MyUserViewHolder> {
    public static final String EXTRA_URL = "imgUrl";
    public static final String EXTRA_CREATOR = "creator";
    public static final String VIEWS = "views";
    public static final String USERIMGNODE = "userNode";
    public static final String IMG_IN_ALL_USER = "allUserNode";
    public static final String NO_OF_DOWNLOADS = "numberOfDownloads";
    public static final String USER_ID = "userId";

    private ArrayList<CurrentUserImgDetails> userImgDetails;
    private Context context;

    public CurrentUserImgShowAdapter(ArrayList<CurrentUserImgDetails> userImgDetails, Context context) {
        this.userImgDetails = userImgDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public MyUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_firebase, parent, false);

        return new MyUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserViewHolder holder, int position)
    {
        CurrentUserImgDetails details = userImgDetails.get(position);

        if (details == null)
        {
            holder.tv_creator.setText("NOTHING FOUND");
        }

        if (details != null) {
            holder.tv_creator.setText(details.getUserName());
        }
        holder.tv_likes.setText(String.valueOf(details.getViews()));
        holder.tv_downloads.setText(String.valueOf(details.getDownloads()));
        Glide.with(context)
                .load(Uri.parse(details.getImgUrl()))
                .into(holder.img_to_display);
    }

    @Override
    public int getItemCount()
    {
        return userImgDetails.size();
    }

    public class MyUserViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView img_to_display;
        public TextView tv_creator, tv_likes,tv_downloads;

        public MyUserViewHolder(View itemView) {
            super(itemView);

            img_to_display = itemView.findViewById(R.id.iv_google_user);
            tv_creator = itemView.findViewById(R.id.tv_userName);
            tv_likes = itemView.findViewById(R.id.tv_views);
            tv_downloads = itemView.findViewById(R.id.tv_downloads);


            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    CurrentUserImgDetails details = userImgDetails.get(getAdapterPosition());

                    Intent gotoImg = new Intent(context, UserImgDetails.class);

                    gotoImg.putExtra(EXTRA_URL, details.getImgUrl());
                    gotoImg.putExtra(EXTRA_CREATOR, details.getUserName());
                    gotoImg.putExtra(VIEWS, details.getViews());
                    gotoImg.putExtra(USERIMGNODE, details.getImgNode());
                    gotoImg.putExtra(IMG_IN_ALL_USER, details.getAllImgNodeId());
                    gotoImg.putExtra(NO_OF_DOWNLOADS, details.getDownloads());
                    gotoImg.putExtra(USER_ID, details.getUserId());

                    gotoImg.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    gotoImg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    gotoImg.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(gotoImg);
                }
            });
        }
    }
}
