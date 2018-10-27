package com.newware.wonderimage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class CurentUserActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final int PICK_CODE = 234;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private GoogleSignInAccount googleSignInAccount;
    private DatabaseReference databaseReference;

    private Uri imgUriPath;
    String imgDownloadUrl;
    ImageView uploaded_img;
    Button btnUpload;
    private String userid;
    private long userImgCount;
    private long allUserImgCount;
    static String ts;

    LinearLayout linearLayout;
    // Button upload;
    private MaterialDialog materialDialog;
    private boolean isConnection;
    private String tag = "bhuvaneshvar";
    private RecyclerView recyclerView;
    Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CurrentUserImgShowAdapter imgShowAdapter;
    private ArrayList<CurrentUserImgDetails> dataList;
    private String userName;

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            //super.handleMessage(msg);
//            upDateUi();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curent_user);


        linearLayout = findViewById(R.id.ll_currentUser);

        context = CurentUserActivity.this;

        dataList = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
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
            public void onRefresh()
            {
                if (dataList != null && imgShowAdapter != null)
                {
                    dataList.clear();
                    imgShowAdapter.notifyItemRangeRemoved(0,dataList.size());

                }
                Snackbar.make(linearLayout,"Refreshing",Snackbar.LENGTH_SHORT).show();
                gettingDataFRomFireBase();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //checkConnection();

        uploaded_img = findViewById(R.id.iv_addedImg);
        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setVisibility(View.GONE);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userid = firebaseUser.getUid();
            userName = firebaseUser.getDisplayName();
        }

        getSupportActionBar().setTitle("My Collection");
        if (firebaseUser != null) {
            getSupportActionBar().setSubtitle(firebaseUser.getDisplayName());
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D4654")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference(); //root reference


        recyclerView = findViewById(R.id.rv_userCollection);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
//        runnable = new Runnable() {
//            @Override
//            public void run()
//            {
//                gettingDataFRomFireBase();
//            }
//        };
        //Thread thread = new Thread(runnable);
        //gettingDataFRomFireBase();

        Snackbar.make(linearLayout, "Loading Content", Snackbar.LENGTH_SHORT).show();

        imgShowAdapter = new CurrentUserImgShowAdapter(dataList, context);
        // Log.i(tag,"adapter");
        //   Log.i(tag,"adpter set");
        recyclerView.setAdapter(imgShowAdapter);
        swipeRefreshLayout.setRefreshing(false);

        dismissDialog();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList.clear();
    }

    private void gettingDataFRomFireBase() {
        showDialog();

            DatabaseReference rootOfUsers = databaseReference.child("users").child(userid);// pointing to user

        //showDialog();

        final DatabaseReference imgRefrence = rootOfUsers.child("images");//point to user images


        imgRefrence.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                dataList.clear();
                swipeRefreshLayout.setRefreshing(false);
                for (DataSnapshot imgfolder : dataSnapshot.getChildren())
                {
                    String keyPointToNumbers = imgfolder.getKey();

                    Log.i(tag, "got img folder " + keyPointToNumbers);

                    if (keyPointToNumbers != null)
                    {
                        DatabaseReference imgToExplore =
                                imgRefrence.child(keyPointToNumbers);//pointing to now 1,,2,,,3 etc


                        imgToExplore.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FirebaseStorageDataToSend dataFetch =
                                        dataSnapshot.getValue(FirebaseStorageDataToSend.class);


                                if (dataFetch != null)
                                {
                                    String imgUrl = dataFetch.getImgUri();
                                    int viewsOnpic = dataFetch.getViews();
                                    long keyId = dataFetch.getImgNode();
                                    String allkeyNode = dataFetch.getImgNodeOfAll();
                                    int downloads = dataFetch.getDownloads();
                                    String userName = dataFetch.getUserName();
                                    String userId = dataFetch.getUserId();
                                    dataList.add(new CurrentUserImgDetails(userName,imgUrl,viewsOnpic,keyId,allkeyNode,userId,downloads));
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.i(tag, "ups in outer folder  onCancelled");
            }
        });

        //  Log.i(tag,"now out of loop , gettingfirebasedata finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fragment_of_add_img_icon, menu);

        checkConnection();
        if (!isConnection) {
            menu.removeItem(R.id.menu_add_img);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_img: {

                if ((btnUpload != null) && uploaded_img != null) {
                    btnUpload.setVisibility(View.GONE);
                    uploaded_img.setVisibility(View.GONE);
                }
                showFileChooser();
                break;
            }
            case R.id.menu_user_logOut: {

                FirebaseAuth.getInstance().signOut();
                signOut();
                startActivity(new Intent(CurentUserActivity.this, MainActivity.class));
                ActivityCompat.finishAffinity(this);
            }
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        signInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Snackbar.make(linearLayout, "Signed Out", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == PICK_CODE) && (resultCode == RESULT_OK) && (data.getData() != null)) {
            System.out.println("we are in if cond now try block run");
            imgUriPath = data.getData();// this is full path of image

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUriPath);// got image in bitmap
                uploaded_img.setVisibility(View.VISIBLE);
                uploaded_img.setImageBitmap(bitmap);//setting image
                btnUpload.setVisibility(View.VISIBLE);



                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imgUriPath != null) {
                            sendDataToFireBase(imgUriPath);

                        } else {
                            Snackbar.make(linearLayout, "Choose File",
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });


            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void sendDataToFireBase(Uri muri) {
        final Long tsLong = System.currentTimeMillis() / 1000;
        ts = tsLong.toString();
        showDialog();
        //sending img
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(userid);

        final StorageReference imgRef = storageReference.child("images")
                .child(ts); // creating new node for every pic as current time
        UploadTask imgToupload = imgRef.putFile(muri);

        imgToupload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissDialog();
                Snackbar.make(linearLayout, "Failed", Snackbar.LENGTH_LONG)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showFileChooser();
                            }
                        });
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar.make(linearLayout, "Uploaded", Snackbar.LENGTH_LONG).show();
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploaded_img.setVisibility(View.GONE);
                        btnUpload.setVisibility(View.GONE);
                        //upload.setVisibility(View.GONE);
                        imgDownloadUrl = String.valueOf(uri);
                        allUserImgCount = tsLong;
                        userImgCount = tsLong;
                        // Log.i(tag, "got Uri, " + uri);
                        // Log.i(tag, "childs incresed by 1 now -> user , all : " + userImgCount + " , " + allUserImgCount);
                        sendToDataBase(userImgCount, allUserImgCount, imgDownloadUrl);

                    }
                });
            }
        });

    }

    private void sendToDataBase(long userImgCount, long allUserImgCount, String imgDownloadUrl) {
        String allUserSalted = String.valueOf(allUserImgCount) + userid;
        FirebaseStorageDataToSend dataToSend = new FirebaseStorageDataToSend(imgDownloadUrl,
                0,userid,userImgCount,allUserSalted,0,userName);
        Log.i(tag, "In send to data base method , ");
        //one user data
        final DatabaseReference dbrefUser = databaseReference.child("users").child(userid);
        dbrefUser.child("images")
                .child(String.valueOf(userImgCount))
                .setValue(dataToSend).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(tag, "uploaded to user, " + task);
            }
        });

        //all user data

        databaseReference.child("all_user_img")
                .child(allUserSalted)
                .setValue(dataToSend)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(tag, "uploaded to all, " + task);

                    }
                });
        dismissDialog();
        Intent newIn = new Intent(CurentUserActivity.this, CurentUserActivity.class);
        newIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        newIn.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(newIn);
        finish();

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public final void showDialog() {
        materialDialog = new MaterialDialog.Builder(this)
                .title("Loading")
                .titleColorRes(R.color.warning)
                .backgroundColorRes(R.color.colorPrimaryDark)
                .contentColorRes(R.color.colorAccent)
                .cancelable(true)
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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        isConnection = isConnected;
        if (isConnected) {
            Snackbar.make(linearLayout, "Internet connected", Snackbar.LENGTH_LONG).show();
        } else {
            {
                Snackbar.make(linearLayout, "Internet Connection loss", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    private void checkConnection() {
        isConnection = ConnectivityReceiver.isConnected();
        if (!isConnection) {
            Snackbar.make(linearLayout, "Internet Connection loss", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MyApplication.getInstance().setConnectivityListener(this);
        checkConnection();
    }

    @Override
    protected void onResume()
    {
        if (dataList != null && imgShowAdapter != null)
        {
            dataList.clear();
            imgShowAdapter.notifyItemRangeRemoved(0,dataList.size());

        }
        gettingDataFRomFireBase();
        super.onResume();
    }
}

