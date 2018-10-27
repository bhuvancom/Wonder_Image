package com.newware.wonderimage;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 29-07-2018.
 * Copyright (c) 2018
 **/
public class CurrentUserImgDetails {
    String userName;
    String imgUrl;
    int views;
    long imgNode;
    String allImgNodeId;
    String userId;
    int downloads;

    public CurrentUserImgDetails() {
    }

    public CurrentUserImgDetails(String userName, String imgUrl, int views, long imgNode, String allImgNodeId, String userId, int downloads) {
        this.userName = userName;
        this.imgUrl = imgUrl;
        this.views = views;
        this.imgNode = imgNode;
        this.allImgNodeId = allImgNodeId;
        this.userId = userId;
        this.downloads = downloads;
    }

    public String getUserName() {
        return userName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getViews() {
        return views;
    }

    public long getImgNode() {
        return imgNode;
    }

    public String getAllImgNodeId() {
        return allImgNodeId;
    }

    public String getUserId() {
        return userId;
    }

    public int getDownloads() {
        return downloads;
    }
}
