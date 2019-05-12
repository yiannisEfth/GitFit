package com2027.killaz.kalorie.gitfit;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class BmiFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bmi_fragment, container, false);
    }
    private EditText mHeightInput;
    private EditText mWeightInput;
    private Spinner mHeightMeasure;
    private Spinner mWeightMeasure;
    private TextView mHeightInputText;
    private TextView mWeightInputText;
    private TextView mAgeInput_text;
    private EditText mAgeInput;
    private List<String> list1;
    private List<String> list2;
    private Button mSubmit;
    private DatabaseHelper dbHelper;
    private FirebaseUser currentUser;

    //Display
    private TextView mBMI;
    private TextView mMessage;

    /**
     * Method to initiate the number animation for the bmi value
     * @param value the bmi value
     */
    private void startCountAnimation(double value) {
       final DecimalFormat df = new DecimalFormat("#.##");
        ValueAnimator animator = ValueAnimator.ofFloat((float) Double.parseDouble(mBMI.getText().toString()), (float) value);
        animator.setDuration(3500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mBMI.setText(df.format(animation.getAnimatedValue()));
            }
        });
        animator.start();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mHeightInput = (EditText) getView().findViewById(R.id.height_input);
        mWeightInput = (EditText) getView().findViewById(R.id.weight_input);
        mHeightMeasure = (Spinner) getView().findViewById(R.id.height_measure);
        mWeightMeasure = (Spinner) getView().findViewById(R.id.weight_measure);
        mHeightInputText = (TextView) getView().findViewById(R.id.height_input_text);
        mWeightInputText = (TextView) getView().findViewById(R.id.weight_input_text);
        mAgeInput_text = (TextView) getView().findViewById(R.id.age_input_text);
        mAgeInput = (EditText) getView().findViewById(R.id.age_input);
        mSubmit = (Button) getView().findViewById(R.id.submit_button);

        //Display
        mBMI = (TextView) getView().findViewById(R.id.bmi_value);
        mMessage = (TextView) getView().findViewById(R.id.bmi_message);

        mBMI.setVisibility(View.GONE);
        mBMI.setText("0.0");

        list1 = new ArrayList<String>();
        list1.add("cm");
        list1.add("in");

        list2 = new ArrayList<String>();
        list2.add("kg");
        list2.add("lbs");

        // Setup spinner values
        ArrayAdapter<String> adapt1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list1);
        adapt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHeightMeasure.setAdapter(adapt1);

        ArrayAdapter<String> adapt2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list2);
        adapt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeightMeasure.setAdapter(adapt2);

        //Listener for calculate bmi value button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double weight;
                double height;
                double age;

                if (mAgeInput.getText().toString().isEmpty()|| mWeightInput.getText().toString().isEmpty() || mHeightInput.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in the blanks", Toast.LENGTH_SHORT).show();
                }
                else {
                   weight = Double.parseDouble(mWeightInput.getText().toString());
                   height = Double.parseDouble(mHeightInput.getText().toString());
                   age = Double.parseDouble(mAgeInput.getText().toString());

                    if(weight > 800 || weight < 20 || height > 250 || height < 20){
                        Toast.makeText(getActivity(), "Invalid values entered. Please retry", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (age < 13 || age > 65)
                            Toast.makeText(getActivity(),  "You are not in the age range for BMI measurement", Toast.LENGTH_SHORT).show();
                        else
                            calculateBMI(height, weight);
                    }
                }
            }
        });

        // Get the current user from the Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Fetch data from local database if it is saved, otherwise use default values
        dbHelper = DatabaseHelper.getInstance(getContext());
        try {
            mHeightInput.setText(String.valueOf(dbHelper.getUserHeight(currentUser.getDisplayName())));
            mWeightInput.setText(String.valueOf(dbHelper.getUserWeight(currentUser.getDisplayName())));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("USER BMI VALUES", "NOT FOUND. USING DEFAULT.");
        }

    }

    /**
     * Method to calculate BMI value given the necessary input values
     * Output also included a comment based on the calculate BMI value
     */
    public void calculateBMI(double height, double weight) {

        final double bmi;

        if (mHeightMeasure.getSelectedItem() != "cm") {
            height = Double.parseDouble(mHeightInput.getText().toString()) * 2.54;
        }

        if (mWeightMeasure.getSelectedItem() != "kg") {
            weight = Double.parseDouble(mWeightInput.getText().toString()) * 0.45;
        }

        bmi = Math.round(weight / ((height / 100) * (height / 100)) * 10) / 10.0;
        mBMI.setVisibility(View.VISIBLE);
        setVisabilityOfWidgets(false);
        startCountAnimation(bmi);

        final double W = weight;
        final double H = height;
        Snackbar.make(getView(), "Save these values for calorie calculations?", Snackbar.LENGTH_LONG)
                .setAction("Do it!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper = DatabaseHelper.getInstance(getContext());
                        boolean saved = dbHelper.saveRecordsBMI(currentUser.getDisplayName(),(float) W,(float) H,0);
                        if (saved) {
                            Log.d("USER BMI VALUES", "SAVE SUCCESSFULLY");
                            Snackbar.make(getView(), "Saved!", Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            Log.d("USER BMI VALUES", "FAILED TO SAVE");
                            Snackbar.make(getView(), "Values failed to save.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisabilityOfWidgets(true);
                if (bmi < 15.0)
                    mMessage.setText(getString(R.string.very_underweight));
                else if (bmi < 18.5)
                    mMessage.setText(getString(R.string.underweight));
                else if (bmi < 25.0)
                    mMessage.setText(getString(R.string.healthy));
                else if (bmi < 30.0)
                    mMessage.setText(getString(R.string.overweight));
                else
                    mMessage.setText(getString(R.string.obese));
            }
        }, 5000);

    }

    public void setVisabilityOfWidgets(boolean visible){
        if(visible){
            mSubmit.setVisibility(View.VISIBLE);
            mHeightInput.setVisibility(View.VISIBLE);
            mWeightInput.setVisibility(View.VISIBLE);
            mHeightMeasure.setVisibility(View.VISIBLE);
            mWeightMeasure.setVisibility(View.VISIBLE);
            mHeightInputText.setVisibility(View.VISIBLE);
            mWeightInputText.setVisibility(View.VISIBLE);
            mAgeInput_text.setVisibility(View.VISIBLE);
            mAgeInput.setVisibility(View.VISIBLE);
        }
        else{
            mSubmit.setVisibility(View.GONE);
            mHeightInput.setVisibility(View.INVISIBLE);
            mWeightInput.setVisibility(View.INVISIBLE);
            mHeightMeasure.setVisibility(View.INVISIBLE);
            mWeightMeasure.setVisibility(View.INVISIBLE);
            mHeightInputText.setVisibility(View.INVISIBLE);
            mWeightInputText.setVisibility(View.INVISIBLE);
            mAgeInput_text.setVisibility(View.INVISIBLE);
            mAgeInput.setVisibility(View.INVISIBLE);
        }
    }


}