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

public class HomeFragment extends Fragment {

    private TextView stepText;
    private TextView challengeStepsText;
    private ProgressBar challengeBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button btn1 = (Button) getView().findViewById(R.id.homeBtn1);
        Button btn2 = (Button) getView().findViewById(R.id.homeBtn2);
        Button btn3 = (Button) getView().findViewById(R.id.homeBtn3);
        stepText = (TextView) getView().findViewById(R.id.stepTextView);
        challengeBar = (ProgressBar) getView().findViewById(R.id.challenge_bar);
        challengeStepsText = (TextView) getView().findViewById(R.id.challengeStepsText);
    }

    /**
     * Broadcast Receiver to update the UI when a broadcast is sent from FirestoreService.
     */
    private class StepBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast Receiver", "Broadcast Received.");

            int steps = intent.getIntExtra("steps", 0);
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
        getActivity().registerReceiver(new StepBroadcastReceiver(), intentFilter);
        Log.d("Broadcast Receiver", "Receiver Registered.");
    }
}
