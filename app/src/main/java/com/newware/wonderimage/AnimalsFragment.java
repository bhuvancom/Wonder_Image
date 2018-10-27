package com.newware.wonderimage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;


public class AnimalsFragment extends Fragment {
    VolleyHelper volleyHelper;
    RecyclerView recyclerView;
    ArrayList<DataList> dataLists;
    AdapteRecyclerView adapteRecyclerView;
    Context context;
    RequestQueue requestQueue;

    public AnimalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_animals, container, false);

        context = this.getContext();
        recyclerView = v.findViewById(R.id.rv_Oceans);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        dataLists = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(context);
        adapteRecyclerView = new AdapteRecyclerView(dataLists, context);
        recyclerView.setAdapter(adapteRecyclerView);

        volleyHelper = new VolleyHelper(recyclerView, dataLists, adapteRecyclerView, requestQueue, "animals", context);
        //volleyHelper.parseJson();
        volleyHelper.toPerformTask.start();

        return v;
    }

}
