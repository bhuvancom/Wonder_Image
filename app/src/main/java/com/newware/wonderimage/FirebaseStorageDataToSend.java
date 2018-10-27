package com.newware.wonderimage;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 25-07-2018.
 * Copyright (c) 2018
 **/
public class FirebaseStorageDataToSend {

    private String imgUri;
    private int views;
    private String userId;
    private long imgNode;
    private String imgNodeOfAll;
    private int downloads;
    private String userName;


    public FirebaseStorageDataToSend() {
    }

    public FirebaseStorageDataToSend(String imgUri, int views, String userId, long imgNode, String imgNodeOfAll, int downloads, String userName) {
        this.imgUri = imgUri;
        this.views = views;
        this.userId = userId;
        this.imgNode = imgNode;
        this.imgNodeOfAll = imgNodeOfAll;
        this.downloads = downloads;
        this.userName = userName;
    }

    public String getImgUri() {
        return imgUri;
    }

    public int getViews() {
        return views;
    }

    public String getUserId() {
        return userId;
    }

    public long getImgNode() {
        return imgNode;
    }

    public String getImgNodeOfAll() {
        return imgNodeOfAll;
    }

    public int getDownloads() {
        return downloads;
    }

    public String getUserName() {
        return userName;
    }
}
