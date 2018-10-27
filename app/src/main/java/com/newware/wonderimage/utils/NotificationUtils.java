package com.newware.wonderimage.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import com.newware.wonderimage.R;
import com.newware.wonderimage.config.AppConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 05-08-2018.
 * Copyright (c) 2018
 **/
public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timestamp, final Intent intent, String imgUrl) {
        //check for  empty push msg
        if (TextUtils.isEmpty(message))
            return;

        final int appIco = R.drawable.img5; //app icon

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // set to
        final PendingIntent resultPendingIntent =

                PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,"ch_id");//todo:remove second arg if error

        if (!TextUtils.isEmpty(imgUrl))
        {
            if (imgUrl != null && imgUrl.length() > 4 && Patterns.WEB_URL.matcher(imgUrl).matches())
            {
                Bitmap bitmap = getBitmapFromURL(imgUrl);
                if (bitmap != null)
                {
                    showBigNotification(bitmap,mBuilder,appIco,title,message,timestamp,resultPendingIntent);
                }
                else {
                    showSmallNotification(mBuilder,appIco,title,message,timestamp,resultPendingIntent);
                }
            }
            else
            {
                showSmallNotification(mBuilder,appIco,title,message,timestamp,resultPendingIntent);
                playNotificationSound();
            }
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int appIco,
                                       String title, String message, String timestamp, PendingIntent resultPendingIntent)
    {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(appIco).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(inboxStyle)
                .setWhen(getTimeMiliSec(timestamp))
                .setSmallIcon(appIco)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),appIco))
                .setContentText(message)
                .build();
        mBuilder.setLights(0xff00ff00, 300, 100);

        notification.ledARGB = 0xff00ff00;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 200;
        notification.ledOffMS = 200;

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AppConfig.NOTIFICATION_ID,notification);
    }







    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder,
                                     int appIco, String title, String message, String timestamp, PendingIntent resultPendingIntent)
    {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        Notification notification;
        notification = mBuilder.setSmallIcon(appIco)
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMiliSec(timestamp))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),appIco))
                .setContentText(message)
                .setLights(0xff00ff00, 300, 100)

                .build();


        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AppConfig.NOTIFICATION_ID_BIG_IMAGE,notification);
    }


    public static long getTimeMiliSec(String timeStamp)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(timeStamp);
            return date.getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }



    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private Bitmap getBitmapFromURL(String imgUrls)
    {
        try {
            URL url = new URL(imgUrls);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            return BitmapFactory.decodeStream(inputStream);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context)
    {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        else
            {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Ringtone r = RingtoneManager.getRingtone(mContext, uri);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
