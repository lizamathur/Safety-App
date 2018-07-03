package com.example.lizamathur.safetyring;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ActivityRegistered extends AppCompatActivity {
    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
    }
    public void changeActivity2(View v) {
        Intent intentToContacts = new Intent(ActivityRegistered.this, ActivityContact1.class);
        startActivity(intentToContacts);
        finish();
    }
}
