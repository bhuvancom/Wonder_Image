package com.newware.wonderimage;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 24-07-2018.
 * Copyright (c) 2018
 **/
public class DataList {
    String imgUrl;
    String creator;
    int likes;
    String hdImgLink;

    public DataList(String imgUrl, String creator, int likes,String hdImgLink) {
        this.imgUrl = imgUrl;
        this.creator = creator;
        this.likes = likes;
        this.hdImgLink = hdImgLink;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCreator() {
        return creator;
    }

    public int getLikes() {
        return likes;
    }

    public String getHdImgLink() {
        return hdImgLink;
    }
}
