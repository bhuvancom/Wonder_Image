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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class OceanFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<DataList> dataLists;
    private RequestQueue requestQueueVollry;
    private AdapteRecyclerView adapteRecyclerView;
    private Context context;
    private VolleyHelper volleyHelper;


    public OceanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_ocean, container, false);

        context = this.getContext();

        recyclerView = view.findViewById(R.id.rv_Oceans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        dataLists = new ArrayList<>();

        requestQueueVollry = Volley.newRequestQueue(context);
        adapteRecyclerView = new AdapteRecyclerView(dataLists, context);
        recyclerView.setAdapter(adapteRecyclerView);
        volleyHelper = new VolleyHelper(recyclerView, dataLists, adapteRecyclerView, requestQueueVollry, "ocean", context);
        //volleyHelper.parseJson();
        volleyHelper.toPerformTask.start();

        return view;
    }


}
