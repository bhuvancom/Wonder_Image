package com.newware.wonderimage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoardScreen extends AppCompatActivity
{
    //welcomeScreen
    private ViewPager mslidePager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button btn_next,btn_previous,btn_finish;
    private int mCurrentPage;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board_screen);
        sharedPreferences = getSharedPreferences("onBoard", MODE_PRIVATE);
        if (sharedPreferences.getString("onBoard","").equals("onBoardOK"))
        {
            Intent gotoMain = new Intent(OnBoardScreen.this,MainActivity.class);
            startActivity(gotoMain);
            OnBoardScreen.this.finish();
        }

        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        btn_finish = findViewById(R.id.btn_finish);

        mslidePager = findViewById(R.id.vp_viewPager);
        mDotLayout = findViewById(R.id.linear_dots);
        sliderAdapter = new SliderAdapter(this);
        mslidePager.setAdapter(sliderAdapter);
        addDotIndicator(0);
        mslidePager.addOnPageChangeListener(viewListner);


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mslidePager.setCurrentItem(mCurrentPage + 1);
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mslidePager.setCurrentItem(mCurrentPage - 1);

            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("onBoard","onBoardOK");
                editor.apply();
                editor.commit();
                Intent gotoMain = new Intent(OnBoardScreen.this,MainActivity.class);
                startActivity(gotoMain);
                OnBoardScreen.this.finish();
            }
        });


    }
    public void addDotIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++)
        {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorAccent));

            mDotLayout.addView(mDots[i]);

        }
        if (mDots.length > 0)
        {
            mDots[position].setTextColor(getResources().getColor(R.color.warning));
        }

    }

    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position)
        {
            addDotIndicator(position);
            mCurrentPage = position;

            if (position == 0)
            {
                btn_next.setEnabled(true);
                btn_previous.setEnabled(false);
                btn_finish.setVisibility(View.GONE);
                btn_finish.setEnabled(false);
                btn_previous.setVisibility(View.INVISIBLE);


                btn_next.setText("NEXT");
                btn_previous.setText("");
            }

            else if (position == mDots.length - 1)
            {
                btn_next.setEnabled(false);
                btn_previous.setEnabled(true);
                btn_next.setVisibility(View.GONE);
                btn_previous.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.VISIBLE);
                btn_finish.setEnabled(true);
                btn_finish.setText("FINISH");
                btn_next.setText("Finish");
                btn_previous.setText("Back");
            }
            else {
                btn_next.setEnabled(true);
                btn_previous.setEnabled(true);
                btn_finish.setVisibility(View.GONE);
                btn_finish.setEnabled(false);
                btn_previous.setVisibility(View.VISIBLE);


                btn_next.setText("NEXT");
                btn_previous.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
