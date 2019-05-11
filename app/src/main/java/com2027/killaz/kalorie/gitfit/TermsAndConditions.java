package com2027.killaz.kalorie.gitfit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TermsAndConditions extends AppCompatActivity {

    private Button backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        backToLogin = (Button)findViewById(R.id.back_to_login_btn_tnc);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(TermsAndConditions.this, LoginActivity.class);
                startActivity(loginIntent);
                TermsAndConditions.this.finish();
            }
        });
    }
}
