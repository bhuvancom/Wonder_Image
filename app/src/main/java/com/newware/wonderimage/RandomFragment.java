package com.newware.wonderimage;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;


public class RandomFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<DataList> dataLists;
    AdapteRecyclerView adapteRecyclerView;
    private RequestQueue requestQueueVolley;
    Context context;
    VolleyHelper volleyHelper;
    MaterialDialog materialDialog;


    public RandomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_random, container, false);
        context = this.getActivity();
        recyclerView = v.findViewById(R.id.rv_Oceans);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        dataLists = new ArrayList<>();

        requestQueueVolley = Volley.newRequestQueue(context);

        adapteRecyclerView = new AdapteRecyclerView(dataLists, context);
        recyclerView.setAdapter(adapteRecyclerView);

        volleyHelper = new VolleyHelper(recyclerView, dataLists, adapteRecyclerView, requestQueueVolley, "", context);

        //volleyHelper.parseJson();
        volleyHelper.toPerformTask.start();


        return v;
    }

}
