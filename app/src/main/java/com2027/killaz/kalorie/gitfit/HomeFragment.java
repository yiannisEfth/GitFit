package com2027.killaz.kalorie.gitfit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView stepText;
    private TextView timeText;
    private TextView challengeStepsText;
    private ProgressBar challengeBar;
    private StepBroadcastReceiver br;
    private DatabaseHelper dbHelper;
    private int steps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Button todayBtn = (Button) getView().findViewById(R.id.homeBtn1);
        final Button thisWeekBtn = (Button) getView().findViewById(R.id.homeBtn2);
        final Button thisMonthBtn = (Button) getView().findViewById(R.id.homeBtn3);
        stepText = (TextView) getView().findViewById(R.id.stepTextView);
        timeText = (TextView) getView().findViewById(R.id.timeTextView);
        challengeBar = (ProgressBar) getView().findViewById(R.id.challenge_bar);
        challengeStepsText = (TextView) getView().findViewById(R.id.challengeStepsText);

        br = new StepBroadcastReceiver();
        dbHelper = DatabaseHelper.getInstance(getContext());
        //todayBtn.setBackgroundColor(Color.GREEN);

        final DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.UK);

        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepText.setText(String.valueOf(steps));
                timeText.setText(R.string.button_1);
                //todayBtn.setBackgroundColor(Color.GREEN);
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

                    String dateString = dateFormat.format(cal.getTime());
                    Log.v("Getting steps from", dateString);

                    total += dbHelper.getSteps(dateString);
                    Log.v("Updated total", String.valueOf(total));
                }

                stepText.setText(String.valueOf(total));
                timeText.setText(R.string.button_2);
            }
        });

        thisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
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
                int progress = (int) (((total-remaining)*100.0f) / total);
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
    }
}
