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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import static com.newware.wonderimage.AdapteRecyclerView.EXTRA_CREATOR;
import static com.newware.wonderimage.AdapteRecyclerView.EXTRA_LIKES;
import static com.newware.wonderimage.AdapteRecyclerView.EXTRA_URL;
import static com.newware.wonderimage.AdapteRecyclerView.HD_IMAGE;

public class ImageDetailActivity extends AppCompatActivity {
    private ImageView iv_deatiledPic;
    protected TextView tv_creator, tv_likes;
    protected Button btn_download;
    protected String imgUrl;
    protected RelativeLayout relativeLayout;
    protected String creator;
    protected DownloadHelper downloadHelper;
    protected Context context;
    String hdImg;

    private static final int PERMISSION_REQUEST_CODE = 1;
    String wantPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D4654")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relativeLayout = findViewById(R.id.relativeLayout_imgDetails);
        iv_deatiledPic = findViewById(R.id.imageViewDetail);
        tv_creator = findViewById(R.id.tv_creator_detail);
        tv_likes = findViewById(R.id.tv_likesDetail);
        context = ImageDetailActivity.this;

        btn_download = findViewById(R.id.btn_download);
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(EXTRA_URL);
        creator = intent.getStringExtra(EXTRA_CREATOR);
        int likes = intent.getIntExtra(EXTRA_LIKES, 0);
        hdImg = intent.getStringExtra(HD_IMAGE);

        Glide.with(this)
                .load(imgUrl)
                .into(iv_deatiledPic);
        tv_creator.setText(creator);
        tv_likes.setText("Likes : " + likes);


        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String time = String.valueOf(System.currentTimeMillis() / 1000);

                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission(wantPermission)) {
                        requestPermission(wantPermission);
                    } else {
                        DownloadHelper downloadHelper = new DownloadHelper(creator, context, relativeLayout, hdImg, time);
                        downloadHelper.getFile();
                    }
                } else {
                    downloadHelper = new DownloadHelper(creator, context, relativeLayout, hdImg, time);
                    downloadHelper.getFile();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(context, permission);
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
        // if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
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
                ActivityCompat.requestPermissions(ImageDetailActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
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
