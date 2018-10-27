package com.newware.wonderimage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static com.newware.wonderimage.AdapteRecyclerView.EXTRA_CREATOR;
import static com.newware.wonderimage.AdapteRecyclerView.EXTRA_URL;
import static com.newware.wonderimage.CurrentUserImgShowAdapter.IMG_IN_ALL_USER;
import static com.newware.wonderimage.CurrentUserImgShowAdapter.NO_OF_DOWNLOADS;
import static com.newware.wonderimage.CurrentUserImgShowAdapter.USERIMGNODE;
import static com.newware.wonderimage.CurrentUserImgShowAdapter.USER_ID;
import static com.newware.wonderimage.CurrentUserImgShowAdapter.VIEWS;

public class UserImgDetails extends AppCompatActivity {
    private static final String TAG = "permission";
    private ImageView iv_deatiledPic;
    private TextView tv_creator, tv_likes, tv_download;
    private Button btn_delete, btn_download;
    private String tag = "imgDetails";
    protected DownloadHelper downloadHelper;
    protected Context context;
    protected RelativeLayout relativeLayout;


    protected String imgUrl;
    protected String userId;
    protected String ImguserID;
    protected int downloadCount;
    protected int viewsCount;
    protected String creatorName;
    protected String currentImgNode;
    protected String imgNodeInall;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private MaterialDialog materialDialogSpin;


    private static final int PERMISSION_REQUEST_CODE = 1;
    String wantPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_img_details);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D4654")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();


        context = UserImgDetails.this;

        relativeLayout = findViewById(R.id.relativeLayout_user_img);


        iv_deatiledPic = findViewById(R.id.imageViewDetail);

        tv_creator = findViewById(R.id.tv_creator_detail);
        tv_likes = findViewById(R.id.tv_likesDetail);
        tv_download = findViewById(R.id.tv_downloadDetails);

        btn_delete = findViewById(R.id.btn_delete);
        btn_download = findViewById(R.id.btn_download_currentImg);


        Intent intent = getIntent();

        imgUrl = intent.getStringExtra(EXTRA_URL);
        creatorName = intent.getStringExtra(EXTRA_CREATOR);
        viewsCount = intent.getIntExtra(VIEWS, 0);
        ImguserID = intent.getStringExtra(USER_ID);
        downloadCount = intent.getIntExtra(NO_OF_DOWNLOADS, 0);
        final long nodePosition = intent.getLongExtra(USERIMGNODE, 0);
        final String nodePosInAll = intent.getStringExtra(IMG_IN_ALL_USER);

        currentImgNode = String.valueOf(nodePosition);
        imgNodeInall = nodePosInAll;


        if (!(userId.equals(ImguserID))) {
            btn_delete.setVisibility(View.GONE);
        }


        //view counter
        if (!(userId.equals(ImguserID))) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference dbRefToViewer = databaseReference.child("viewers").child(userId).child(String.valueOf(nodePosition));//point ot viewer id
            dbRefToViewer.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!(dataSnapshot.hasChild(String.valueOf(nodePosition)))) //if this img is not present
                    {
                        Map<String, Object> updatingViews = new HashMap<>();
                        updatingViews.put("views", (viewsCount + 1)); //increase count of image view by 1

                        DatabaseReference refTouserImg = databaseReference.child("users").child(ImguserID)
                                .child("images").child(String.valueOf(nodePosition));

                        DatabaseReference refToall = databaseReference.child("all_user_img").child(nodePosInAll);

                        refToall.updateChildren(updatingViews); // update in all user view
                        refTouserImg.updateChildren(updatingViews); // update in current user view

                        //saving new node as viewer
                        Map<String, Object> addTodb = new HashMap<>();
                        addTodb.put(String.valueOf(nodePosition), "imgNodeId");
                        dbRefToViewer.setValue(addTodb);
                        viewsCount++;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        Glide.with(this)
                .load(imgUrl)
                .into(iv_deatiledPic);
        tv_creator.setText(creatorName);
        tv_likes.setText(String.valueOf(viewsCount));
        tv_download.setText(String.valueOf(downloadCount));

        Log.i(tag, "got id userPos , allPos " + nodePosition + " , " + nodePosInAll);

        btn_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(UserImgDetails.this);
                materialDialog.canceledOnTouchOutside(false)
                        .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                        .title("Delete this Image ?")
                        .titleColor(getResources().getColor(R.color.warning))
                        .content("Clicking Delete will delete this image permanently !\nContinue ?")
                        .contentColor(getResources().getColor(R.color.colorAccent))
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .negativeColor(getResources().getColor(R.color.warning))
                        .positiveColor(getResources().getColor(R.color.colorAccent))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                showDialog();
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference();//root of db

                                DatabaseReference toDeleteFromUser = databaseReference.child("users").child(userId).child("images");//point to img
                                toDeleteFromUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        showDialog();
                                        DataSnapshot ds = dataSnapshot.child(String.valueOf(nodePosition));
                                        ds.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError,
                                                                   @NonNull DatabaseReference databaseReference) {
                                            }
                                        });
                                        dismissDialog();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                DatabaseReference toDelFromAll = databaseReference.child("all_user_img");//point to all user img node

                                toDelFromAll.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        showDialog();
                                        DataSnapshot ds = dataSnapshot.child(String.valueOf(nodePosInAll));
                                        ds.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError,
                                                                   @NonNull DatabaseReference databaseReference) {
                                                dismissDialog();
//

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        dismissDialog();

                                    }
                                });
                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(userId);
                                StorageReference imgref = storageReference.child("images").child(String.valueOf(nodePosition));
                                imgref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        onBackPressedBy();
                                        dismissDialog();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
                dismissDialog();

            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String timesss = String.valueOf(System.currentTimeMillis() / 1000);

                Log.i("btn", "btn pressed");
                if (Build.VERSION.SDK_INT >= 23) {
                    Log.i("btn", "sdk is  > 23");
                    if (!checkPermission(wantPermission)) {
                        Log.i("btn", "Permission");
                        requestPermission(wantPermission);
                    } else {
                        DownloadHelper downloadHelper = new DownloadHelper(creatorName, context, relativeLayout, imgUrl, currentImgNode);
                        downloadHelper.getFile();
                        increaseDownloadCount();
                    }
                } else {

                    DownloadHelper downloadHelper = new DownloadHelper(creatorName, context, relativeLayout, imgUrl, currentImgNode);
                    downloadHelper.getFile();
                    increaseDownloadCount();


                }

            }
        });
    }

    public void increaseDownloadCount() {
        Map<String, Object> update = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refTocurrent = databaseReference.child("users").child(ImguserID).child("images");
        DatabaseReference refToCurrentImg = refTocurrent.child(currentImgNode);

        DatabaseReference refToall = databaseReference.child("all_user_img");
        DatabaseReference refToImg = refToall.child(imgNodeInall);

        update.put("downloads", (downloadCount + 1));
        downloadCount++;
        tv_download.setText(String.valueOf(downloadCount));

        refToCurrentImg.updateChildren(update);
        refToImg.updateChildren(update);

    }

    public void onBackPressedBy() {
        Intent a = new Intent(this, CurentUserActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        a.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(a);
        finish();

        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public final void showDialog() {
        materialDialogSpin = new MaterialDialog.Builder(this)
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
        if (materialDialogSpin != null) {
            materialDialogSpin.dismiss();
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i("btn", "in chk permission");
            int result = ContextCompat.checkSelfPermission(context, permission);
            Log.i("btn", "result of permission " + result);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestPermission(final String permission) {
        Log.i("btn", "asking permisson" +
                "");
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
//        {
        Log.i("btn", "In permission");
        MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(this);
        materialDialog.title("Accept Permissions")
                .titleColor(getResources().getColor(R.color.warning))
                .content("To save file we need Write permission to Storage")
                .contentColor(getResources().getColor(R.color.colorAccent))
                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                .positiveText("OK")
                .negativeText("Cancel")
                .positiveColor(getResources().getColor(R.color.colorAccent))
                .negativeColor(getResources().getColor(R.color.warning))
                .show();
        materialDialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                Snackbar.make(relativeLayout, "Sorry can't save file without write Permission", Snackbar.LENGTH_LONG).show();
            }
        });
        materialDialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                ActivityCompat.requestPermissions(UserImgDetails.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
            }
        });
    }

    //}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(relativeLayout, "Click on Download Button Again ! ", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(relativeLayout, "Sorry can't save file without write Permission", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (downloadHelper != null) {
            downloadHelper.cancelDownload();
        }
        super.onBackPressed();
    }
}
