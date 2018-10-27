package com.newware.wonderimage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Bhuvaneshvar Nath Srivastava on 24-07-2018.
 * Copyright (c) 2018
 **/
public class VolleyHelper {
    private RecyclerView recyclerView;
    private ArrayList<DataList> dataLists;
    private AdapteRecyclerView adapteRecyclerView;
    private RequestQueue requestQueue;
    private Context context;
    private String url;
    MaterialDialog materialDialog;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);

            updateUi();
        }
    };

    public VolleyHelper(RecyclerView recyclerView,
                        ArrayList<DataList> dataLists,
                        AdapteRecyclerView adapteRecyclerView,
                        RequestQueue requestQueue, String url, Context context) {
        this.recyclerView = recyclerView;
        this.dataLists = dataLists;
        this.adapteRecyclerView = adapteRecyclerView;
        this.requestQueue = requestQueue;
        this.url = url;
        this.context = context;
    }

    Runnable runTask = new Runnable() {
        @Override
        public void run() {
            parseJson();
        }
    };
    Thread toPerformTask = new Thread(runTask);

    public void parseJson() {
        //showDialog();

        // System.out.println("got img string "+url);
        String jsonUrl = "https://pixabay.com/api/?key=9624659-62d5b90c6a59fc1e3eba22599&q=" + url;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, jsonUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                System.out.println("in Json object");
                try {

                    JSONArray jsonArray = response.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imgUrls = jsonObject.getString("webformatURL");
                        String creatorName = jsonObject.getString("user");
                        int likess = jsonObject.getInt("likes");
                        String hdImgLik = jsonObject.getString("fullHDURL");

                        dataLists.add(new DataList(imgUrls, creatorName, likess,hdImgLik));
                    }
                    if (dataLists.isEmpty())
                    {
                        String imgNone = "https://techreviewpro-techreviewpro.netdna-ssl.com/wp-content/uploads//2015/06/Funny-404-File-Not-Found-Error-Page.jpg";
                        dataLists.add(new DataList(imgNone, "Nothing Found", 0,imgNone));
                    }
                    adapteRecyclerView.notifyDataSetChanged();
//                   // System.out.println("Adapter setting");
//                    adapteRecyclerView = new AdapteRecyclerView(dataLists,context.getApplicationContext());
//                    recyclerView.setAdapter(adapteRecyclerView);
//                    adapteRecyclerView.notifyDataSetChanged();
//                  //  System.out.println("adapter set");
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // System.out.println("req for adding");
        requestQueue.add(jsonObjectRequest);
        // System.out.println("added");
    }

    protected void updateUi() {
        // System.out.println("Adapter setting");
        // adapteRecyclerView = new AdapteRecyclerView(dataLists, context);//.getApplicationContext());
        // recyclerView.setAdapter(adapteRecyclerView);
        adapteRecyclerView.notifyDataSetChanged();
        //  System.out.println("adapter set");
    }


    public final void showDialog() {
        materialDialog = new MaterialDialog.Builder(context)
                .title("Loading")
                .titleColorRes(R.color.warning)
                .backgroundColorRes(R.color.colorPrimaryDark)
                .contentColorRes(R.color.colorAccent)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content("Please Wait...")
                .progress(true, 0)
                .show();
    }

    public final void dismissDialog() {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
    }


}
