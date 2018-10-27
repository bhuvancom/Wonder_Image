package com.newware.wonderimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 31-07-2018.
 * Copyright (c) 2018
 **/
public class DownloadHelper {
    private static final String TAG = "Permission";
    private String creator;
    private Context context;
    private View view;
    private String urlToDownload;
    private String ts;
    protected String result = "";
    protected DownloadTask downloadTask;

    public DownloadHelper(String creator, Context context, View view, String urlToDownload, String ts) {
        this.creator = creator;
        this.context = context;
        this.view = view;
        this.urlToDownload = urlToDownload;
        this.ts = ts;
    }

    protected void getFile() {
        downloadTask = new DownloadTask();
        downloadTask.execute(urlToDownload);
    }

    protected void cancelDownload() {
        if (downloadTask != null)
        {
            downloadTask.cancel(true);
        }
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;
        MaterialDialog materialDialog;

        @Override
        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setTitle("");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.setMax(100);
//            progressDialog.setProgress(0);
            // progressDialog.show();


            materialDialog = new MaterialDialog.Builder(context)
                    .title("Downloading")
                    .progress(false, 100, false)
                    .content("Download In Progress\nDepending upon Network and Image quality, Downloading may take time")
                    .titleColor(context.getResources().getColor(R.color.warning))
                    .contentColor(context.getResources().getColor(R.color.colorAccent))
                    .backgroundColor(context.getResources().getColor(R.color.colorPrimary))
                    .widgetColor(context.getResources().getColor(R.color.warning))
                    .negativeText("Cancel")
                    .negativeColor(context.getResources().getColor(R.color.warning))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            cancel(true);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        @Override
        protected void onCancelled()
        {
            cancel(true);
            super.onCancelled();

            Snackbar.make(view,"Download cancelled",Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... voids)
        {

            String path = voids[0]; // we have send this data so it is at index 0
            int fileLength = 0;  // set file length to 0
            try {

                URL url = new URL(path); // setting url

                URLConnection urlConnection = url.openConnection(); // open connection with that url

                urlConnection.connect();  //connect

                fileLength = urlConnection.getContentLength(); //get file length

                File new_Folder = new File("sdcard/newWare"); // pointing to directory

                if ((!new_Folder.exists())) { // checking if this not exist

                    new_Folder.mkdir(); // if not exist create new
                }
                File newFol = new File(new_Folder, "Wonder Gallery");
                if (!newFol.exists()) {
                    newFol.mkdir();
                }
                File creatorName = new File(newFol, creator);
                if (!(creatorName.exists())) {
                    creatorName.mkdir();
                }

                File inputFile = new File(creatorName, "download" + ts + ".jpg");// adding file to this directory

                if (!(inputFile.exists())) {

                    InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);//opening buffer to add file bytes it is now 8kb

                    byte[] data = new byte[1024];
                    int total = 0;
                    int count = 0;


                    OutputStream outputStream = new FileOutputStream(inputFile); //to write file

                    while ((count = inputStream.read(data)) != -1)
                    {
                        if (isCancelled())
                        {
                            outputStream.close();
                            inputStream.close();
                            if (inputFile.exists())
                            {
                                inputFile.delete();
                            }
                            break;
                        }
                        total += count;
                        outputStream.write(data, 0, count);
                        int progress = total * 100 / fileLength;
                        publishProgress(progress);
                    }

                    inputStream.close();
                    outputStream.close();
                }

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                return "Server Error";
            } catch (IOException e)
            {

                e.printStackTrace();
                return "Server Error";
            }
            return "Download Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // progressDialog.setProgress(values[0]);
            materialDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String resultwa) {
            materialDialog.setContent("Done");
            materialDialog.dismiss();
            Snackbar.make(view, resultwa, Snackbar.LENGTH_LONG).show();
            if (resultwa.equals("Download Complete"))
            {result = "file://" + "sdcard/newWare/Wonder Gallery/" + creator + "/" + "download" + ts + ".jpg";
            Toast.makeText(context, "saved to " + result, Toast.LENGTH_LONG).show();}

        }
    }

}
