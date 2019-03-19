package com2027.killaz.kalorie.gitfit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView stepText;
    private TextView timeText;
    private TextView challengeStepsText;
    private ProgressBar challengeBar;
    private StepBroadcastReceiver br;
    private DatabaseHelper dbHelper;
    private String username;
    private int steps;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        final Button todayBtn = (Button) getView().findViewById(R.id.homeBtn1);
        final Button thisWeekBtn = (Button) getView().findViewById(R.id.homeBtn2);
        final Button thisMonthBtn = (Button) getView().findViewById(R.id.homeBtn3);
        stepText = (TextView) getView().findViewById(R.id.stepTextView);
        timeText = (TextView) getView().findViewById(R.id.timeTextView);
        challengeBar = (ProgressBar) getView().findViewById(R.id.challenge_bar);
        challengeStepsText = (TextView) getView().findViewById(R.id.challengeStepsText);
        mAuth = FirebaseAuth.getInstance();
        br = new StepBroadcastReceiver();
        dbHelper = DatabaseHelper.getInstance(getContext());
        todayBtn.setBackgroundColor(0xBBB2FF59);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        username = mAuth.getCurrentUser().getDisplayName();

        drawGraph();

        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepText.setText(String.valueOf(steps));
                timeText.setText(R.string.button_1);
                todayBtn.setBackgroundColor(0xBBB2FF59);
                thisWeekBtn.setBackgroundResource(android.R.drawable.btn_default);
                thisMonthBtn.setBackgroundResource(android.R.drawable.btn_default);
            }
        });

        thisWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());

                // Step backwards through the week until Monday.
                // Add up each day's steps to get total value.
                int total = steps;
                while (cal.get(Calendar.DAY_OF_WEEK) != cal.getFirstDayOfWeek()) {
                    cal.add(Calendar.DAY_OF_WEEK, -1);

                    Log.v("Getting steps from", cal.getTime().toString());

                    total += dbHelper.getSteps(username, cal.getTime());
                    Log.v("Updated total", String.valueOf(total));
                }

                stepText.setText(String.valueOf(total));
                timeText.setText(R.string.button_2);

                thisWeekBtn.setBackgroundColor(0xBBB2FF59);
                todayBtn.setBackgroundResource(android.R.drawable.btn_default);
                thisMonthBtn.setBackgroundResource(android.R.drawable.btn_default);
            }
        });

        thisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());

                // Step backwards through the month until the 1st.
                // Add up each day's steps to get total value.
                int total = steps;
                while (cal.get(Calendar.DAY_OF_MONTH) != 1) {
                    cal.add(Calendar.DAY_OF_WEEK, -1);
                    Log.v("Getting steps from", cal.getTime().toString());

                    total += dbHelper.getSteps(username, cal.getTime());
                    Log.v("Updated total", String.valueOf(total));
                }

                // Once more for the 1st of the month.
                cal.add(Calendar.DAY_OF_WEEK, -1);
                Log.v("Getting steps from", cal.getTime().toString());
                total += dbHelper.getSteps(username, cal.getTime());
                Log.v("Updated total", String.valueOf(total));

                stepText.setText(String.valueOf(total));
                timeText.setText(R.string.button_3);

                thisMonthBtn.setBackgroundColor(0xBBB2FF59);
                todayBtn.setBackgroundResource(android.R.drawable.btn_default);
                thisWeekBtn.setBackgroundResource(android.R.drawable.btn_default);
            }
        });
    }

    /**
     * Broadcast Receiver to update the UI when a broadcast is sent from FirestoreService.
     */
    private class StepBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast Receiver", "Broadcast Received.");

            steps = intent.getIntExtra("steps", 0);
            int remaining = intent.getIntExtra("remaining", 0);
            int total = intent.getIntExtra("challengeTotal", 0);

            stepText.setText(Integer.toString(steps));

            Log.d("Total", String.valueOf(total));
            Log.d("Remaining", String.valueOf(remaining));

            if (total != 0) {
                int progress = (int) (((total - remaining) * 100.0f) / total);
                challengeStepsText.setText("Your progress: " + (total - remaining) + " / " + total);
                Log.d("Challenge Progress", String.valueOf(progress));

                if (progress > 100) {
                    progress = 100;
                }

                challengeBar.setProgress(progress);
            }

        }
    }

    /**
     * This method adds data to the chart view and displays it on the home page.
     */
    public void drawGraph() {
        BarChart chart = (BarChart) getView().findViewById(R.id.chart);

        List<BarEntry> entries = new ArrayList<>();

        // Show 1 week of data
        // This is only for testing
        entries.add(new BarEntry(1,1));
        entries.add(new BarEntry(2,2));
        entries.add(new BarEntry(3,3));
        entries.add(new BarEntry(4,4));
        entries.add(new BarEntry(5,0));
        entries.add(new BarEntry(6,0));
        entries.add(new BarEntry(7,0));

        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        dataSet.setColor(0xFFA2FF59);
        BarData data = new BarData(dataSet);

        chart.setData(data);
        chart.setBackgroundColor(0x88FFFFFF);
        Description desc = new Description();
        desc.setText("Walk more and track your activity!");
        chart.setDescription(desc);
        chart.setNoDataText("No step data saved, come back tomorrow to see your activity log!");
        chart.invalidate();
    }

    /**
     * Function called when application resumes. Registers StepBroadcastReceiver to update UI.
     */
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com2027.killaz.kalorie.gitfit.STEP_TAKEN");
        getActivity().registerReceiver(br, intentFilter);
        Log.d("Broadcast Receiver", "Receiver Registered.");
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(br);
        Log.d("Broadcast Receiver", "Receiver Unregistered.");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Store users steps so far today in database
        dbHelper.updateRecordSteps(username, calendar.getTime(), steps);
    }

}
