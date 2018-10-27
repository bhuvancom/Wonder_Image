package com.newware.wonderimage.config;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 05-08-2018.
 * Copyright (c) 2018
 **/
public class AppConfig {
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "wonder_gallery";
}
