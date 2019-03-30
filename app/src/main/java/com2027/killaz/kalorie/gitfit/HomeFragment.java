package com2027.killaz.kalorie.gitfit;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView stepText;
    private TextView timeText;
    private TextView challengeStepsText;
    private TextView challengeDesc;
    private ProgressBar challengeBar;
    private StepBroadcastReceiver br;
    private DatabaseHelper dbHelper;
    private String username;
    private int steps;
    private FirebaseAuth mAuth;
    private BarChart chart;
    private boolean viewingToday = true;

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
        challengeDesc = (TextView) getView().findViewById(R.id.challenge_desc);
        mAuth = FirebaseAuth.getInstance();
        br = new StepBroadcastReceiver();
        dbHelper = DatabaseHelper.getInstance(getContext());
        todayBtn.setBackgroundColor(0xBBB2FF59);
        username = mAuth.getCurrentUser().getDisplayName();

        graphInit();
        drawGraph();

        // For testing
        stepText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showInsertStepDialog();
                return true;
            }
        });

        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepText.setText(String.valueOf(steps));
                timeText.setText(R.string.button_1);
                todayBtn.setBackgroundColor(0xBBB2FF59);
                thisWeekBtn.setBackgroundResource(android.R.drawable.btn_default);
                thisMonthBtn.setBackgroundResource(android.R.drawable.btn_default);
                viewingToday = true;
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
                viewingToday = false;
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
                viewingToday = false;
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

            // Get extras
            steps = intent.getIntExtra("steps", 0);
            int total = intent.getIntExtra("challengeTotal", 0);
            int remaining = intent.getIntExtra("remaining", total);

            // Increment the step display separately to the variables
            // This stops errors when taking steps while looking at weekly/monthly values
            if (viewingToday) {
                stepText.setText(String.valueOf(steps));
            }
            else if (!stepText.getText().toString().equals(getString(R.string.steps_loading))) {
                stepText.setText(String.valueOf(Integer.parseInt(stepText.getText().toString()) + 1));
            }

            Log.d("Total", String.valueOf(total));
            Log.d("Remaining", String.valueOf(remaining));

            // Show challenge progress.
            if (total > 0) {
                int stepsSoFar = total - remaining;
                if (stepsSoFar < 0) {
                    stepsSoFar = 0;
                    // Just wait for the service to realise a new challenge is needed?
                }

                challengeDesc.setText(getResources().getString(R.string.challenge_desc, total));
                challengeStepsText.setText(getResources().getString(R.string.your_progress, stepsSoFar, total));

                int progress = (int) ((stepsSoFar * 100.0f) / total);

                if (progress >= 100) {
                    progress = 100;
                    Toast.makeText(getContext(), "Challenge complete!", Toast.LENGTH_LONG).show();
                } else {
                    ProgressBarAnimation animate = new ProgressBarAnimation(challengeBar, challengeBar.getProgress(), progress);
                    animate.setDuration(1000);
                    challengeBar.startAnimation(animate);
                }

                Log.d("CHALLENGE_PROGRESS", String.valueOf(progress));

                challengeBar.setProgress(progress);
            }
        }
    }

    /**
     * This method adds data to the chart view and displays it on the home page.
     */
    public void graphInit() {
        chart = (BarChart) getView().findViewById(R.id.chart);

        // Set X axis labels
        final List<String> xAxisLabels = new ArrayList<>();
        xAxisLabels.add("Mon");
        xAxisLabels.add("Tue");
        xAxisLabels.add("Wed");
        xAxisLabels.add("Thu");
        xAxisLabels.add("Fri");
        xAxisLabels.add("Sat");
        xAxisLabels.add("Sun");

        final XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabels.get((int) value);
            }
        });

        chart.setBackgroundColor(0x88FFFFFF);
        chart.setNoDataText("No step data saved, try again later.");
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }

    public void drawGraph() {
        List<BarEntry> entries = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        // Move the calendar back to monday.
        while (cal.get(Calendar.DAY_OF_WEEK) != cal.getFirstDayOfWeek()) {
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }

        // Step through the week, adding each day's steps
        for (int i = 1; i <= 7; i++) {
            entries.add(new BarEntry(i - 1, dbHelper.getSteps(username, cal.getTime())));
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        dataSet.setColor(0xFFA2FF59);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        chart.setData(data);
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

        // Fragment resumes - need to retrieve steps again.
        // TODO get data from firebase
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

    // A method to manually insert steps into the database
    // For testing purposes only
    private void showInsertStepDialog() {
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.insert_steps_dialog, null);
        final DatePicker datePicker = (DatePicker) customView.findViewById(R.id.datepicker);
        final EditText editText = (EditText) customView.findViewById(R.id.stepsEditText);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customView); // Set the view of the dialog to your custom layout
        builder.setTitle("Add steps");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                Calendar cal = new GregorianCalendar(year, month, dayOfMonth);

                int steps = Integer.parseInt(editText.getText().toString());

                // Insert it into the database
                dbHelper.updateRecordSteps(username, cal.getTime(), steps);
                drawGraph();
                dialog.dismiss();
            }});

        // Create and show the dialog
        builder.create().show();
    }

}
