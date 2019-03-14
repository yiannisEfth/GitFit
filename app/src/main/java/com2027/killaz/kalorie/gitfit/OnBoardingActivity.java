package com2027.killaz.kalorie.gitfit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The activity used to display the introductory information for the app.
 */
public class OnBoardingActivity extends AppCompatActivity {

    /**
     * Declare the fields to be used.
     */
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter mSliderAdapter;
    private TextView[] mDots;
    private Button mNextBtn;
    private Button mPreviousBtn;
    private int mCurrentPage;

    /**
     * Initialise the fields and set on click listeners for the buttons in order to navigate to different pages.
     * Also set the created slider adapter for the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        mSlideViewPager = (ViewPager) findViewById(R.id.onBoardingActPager);
        mDotLayout = (LinearLayout) findViewById(R.id.onBoardingActDots);
        mPreviousBtn = (Button) findViewById(R.id.onBoardingBackBtn);
        mNextBtn = (Button) findViewById(R.id.onBoardingNextBtn);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPage == 2) {
                    Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    OnBoardingActivity.this.finish();
                }
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        mSliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(mSliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    /**
     * Method used to add a dot appropriately to the currently viewed page.
     *
     * @param position Gets the position of the current page in order to add the dot appropriately.
     */
    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.onBoardingColorTransparentWhite));
            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.onBoardingColorWhite));
        }
    }

    /**
     * Override the onPageChangeListener in order to alter the buttons visibility and text appropriately for each shown slide.
     */
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;

            if (i == 0) {
                mNextBtn.setEnabled(true);
                mPreviousBtn.setEnabled(false);
                mPreviousBtn.setVisibility(View.INVISIBLE);
                mNextBtn.setVisibility(View.VISIBLE);
                mNextBtn.setText(R.string.on_board_activity_next_btn);
            } else if (i == mDots.length - 1) {
                mNextBtn.setEnabled(true);
                mPreviousBtn.setEnabled(true);
                mPreviousBtn.setVisibility(View.VISIBLE);
                mNextBtn.setText(R.string.on_board_activity_finish_btn);
                mPreviousBtn.setText(R.string.on_board_activity_back_btn);
            } else {
                mNextBtn.setEnabled(true);
                mPreviousBtn.setEnabled(true);
                mPreviousBtn.setVisibility(View.VISIBLE);
                mNextBtn.setText(R.string.on_board_activity_next_btn);
                mPreviousBtn.setText(R.string.on_board_activity_back_btn);

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}

