package com.newware.wonderimage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OtherUserCollectionActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    Context context;
    private CurrentUserImgShowAdapter imgShowAdapter;
    private ArrayList<CurrentUserImgDetails> dataList;
    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    private MaterialDialog materialDialog;
    private final String tag = "bhuvInOthrUsrsClection";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_collection);

        recyclerView = findViewById(R.id.rv_OtherUserCollection);
        getSupportActionBar().setTitle("Users Collection");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D4654")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = OtherUserCollectionActivity.this;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(); //root reference

        linearLayout = findViewById(R.id.ll_otherUsers);

        Snackbar.make(linearLayout, "Loading Content", Snackbar.LENGTH_SHORT).show();
        dataList = new ArrayList<>();

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        //gettingDataFRomFireBase();
                                    }
                                }
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dataList != null && imgShowAdapter != null) {
                    dataList.clear();
                    imgShowAdapter.notifyItemRangeRemoved(0, dataList.size());

                }
                Snackbar.make(linearLayout,"Refreshing",Snackbar.LENGTH_SHORT).show();
                getdata();
                swipeRefreshLayout.setRefreshing(false);
                //gettingDataFRomFireBase();
            }
        });

        imgShowAdapter = new CurrentUserImgShowAdapter(dataList, context);
        // Log.i(tag,"adapter");
        //   Log.i(tag,"adpter set");
        recyclerView.setAdapter(imgShowAdapter);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList.clear();
    }
    public final void showDialog()
    {
        materialDialog = new MaterialDialog.Builder(this)
                .title("Loading")
                .titleColorRes(R.color.warning)
                .backgroundColorRes(R.color.colorPrimaryDark)
                .contentColorRes(R.color.colorAccent)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("Please Wait...")
                .progress(true, 0)
                .show();
    }

    public final void dismissDialog() {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
    }

    public void getdata()
    {
        showDialog();
        final DatabaseReference rootOfUsers = databaseReference.child("all_user_img");// pointing to main folder
        dataList.clear();
        rootOfUsers.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                dataList.clear();
                swipeRefreshLayout.setRefreshing(false);
                for (DataSnapshot imgfolder : dataSnapshot.getChildren()) {
                    String keyPointToNumbers = imgfolder.getKey();

                    Log.i(tag, "got img folder " + keyPointToNumbers);

                    if (keyPointToNumbers != null) {

                        DatabaseReference imgToExplore =
                                rootOfUsers.child(keyPointToNumbers);//pointing to now 1,,2,,,3 etc


                        imgToExplore.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                               // dataList.clear();
                                FirebaseStorageDataToSend dataFetch =
                                        dataSnapshot.getValue(FirebaseStorageDataToSend.class);


                                if (dataFetch != null) {
                                    String imgUrl = dataFetch.getImgUri();
                                    int viewsOnpic = dataFetch.getViews();
                                    long keyId = dataFetch.getImgNode();
                                    String allkeyNode = dataFetch.getImgNodeOfAll();
                                    int downloads = dataFetch.getDownloads();
                                    String userName = dataFetch.getUserName();
                                    String userId = dataFetch.getUserId();
                                    dataList.add(new CurrentUserImgDetails(userName, imgUrl, viewsOnpic, keyId, allkeyNode, userId, downloads));
//                                    Log.i(tag,"got data now showing in recycler view -> name , uri , likes -> "+
//                                            finalUserName +" , "+ imgUrl +" , "+likesOnPic);
                                }

                                imgShowAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                dismissDialog();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i(tag, "ups in img folder inner OnCancelled");
                            }
                        });

                    }

                }

                swipeRefreshLayout.setRefreshing(false);
                dismissDialog();
                //handler.sendEmptyMessage(0);
                //   Log.i(tag,"now out of loop , dialog just going to finish");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(tag, "ups in outer folder  onCancelled");
            }
        });

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    protected void onResume() {
        if (dataList != null && imgShowAdapter != null) {
            dataList.clear();
            imgShowAdapter.notifyItemRangeRemoved(0, dataList.size());

        }
        getdata();
        super.onResume();
    }

}
