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


public class SearchResultFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<DataList> dataLists;
    private RequestQueue requestQueueVollry;
    private AdapteRecyclerView adapteRecyclerView;
    private Context context;
    private VolleyHelper volleyHelper;


    public SearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String strtext = null;

        MainActivity afterSearchActivity = (MainActivity) getActivity();
        if (afterSearchActivity != null) {
            strtext = afterSearchActivity.getMyData();
        }

        System.out.println(strtext);

        // Toast.makeText(context, "text is : "+strtext, Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_result, container, false);

        context = this.getContext();

        recyclerView = v.findViewById(R.id.rv_Oceans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        dataLists = new ArrayList<>();

        requestQueueVollry = Volley.newRequestQueue(context);

//        ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("wait searching");
//        progressDialog.show();
        adapteRecyclerView = new AdapteRecyclerView(dataLists, context);
        recyclerView.setAdapter(adapteRecyclerView);

        volleyHelper = new VolleyHelper(recyclerView, dataLists, adapteRecyclerView, requestQueueVollry, strtext, context);
        //volleyHelper.parseJson();
        volleyHelper.toPerformTask.start();


        return v;
    }


}

