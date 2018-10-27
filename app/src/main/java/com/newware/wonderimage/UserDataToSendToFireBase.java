package com.newware.wonderimage;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 25-07-2018.
 * Copyright (c) 2018
 **/
public class UserDataToSendToFireBase {
    String name_user;
    String email;
    String profileImg;

    public UserDataToSendToFireBase() {
    }

    public UserDataToSendToFireBase(String name_user, String email, String profileImg) {
        this.name_user = name_user;
        this.email = email;
        this.profileImg = profileImg;
    }

    public String getName_user() {
        return name_user;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImg() {
        return profileImg;
    }
}
