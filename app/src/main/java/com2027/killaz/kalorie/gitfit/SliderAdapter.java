package com2027.killaz.kalorie.gitfit;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * SliderAdapter class used to inflate and create each page/slide for the onBoardingActivity
 * along with its appropriate image, title and description.
 */

public class SliderAdapter extends PagerAdapter {
    /**
     * Declare the context and the layout inflater.
     */
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    /**
     * Constructor to take the context.
     *
     * @param c Context
     */
    public SliderAdapter(Context c) {
        mContext = c;
    }

    /**
     * Array for the images to be used.
     */
    public int[] slide_images = {
            R.drawable.on_board_act_pic1,
            R.drawable.on_board_act_pic2,
            R.drawable.on_board_act_pic3
    };
    /**
     * Array for the titles to be used.
     */
    public String[] slide_headings = {
            "BURN THOSE CALORIES",
            "TRACK YOUR PROGRESS",
            "COMPETE WITH FRIENDS"
    };
    /**
     * Array for the descriptions to be used.
     */
    public String[] slide_descs = {
            "Welcome to GitFit! The ultimate fitness app! Lets get to it and burn some calories!",
            "You can track your progress all the time and check how many calories you've burned, the distance you've traveled, and much more!",
            "You can also challenge friends to complete many activities! If you feel more competitive, you can try your luck against the world on the leaderboards!"
    };

    /**
     * Get the count of the slides.
     *
     * @return The number of views available, in this case the size of each of our arrays.
     */
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    /**
     * Determines whether a page view is associated with a specific key object.
     *
     * @param view   Page view to check for association with object.
     * @param object Object to check for association with view.
     * @return True if view is associated with the key object.
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    /**
     * Instantiate each page. Setting appropriate array items using the position of each slide in order to display the correct combination of image, title and description.
     *
     * @param container The containing view in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return An object representing the new page.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.on_boarding_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_pic);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDesc = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDesc.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    /**
     * Remove the page for the given position.
     *
     * @param container The containing view from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The same object that was returned by the instantiateItem method.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
