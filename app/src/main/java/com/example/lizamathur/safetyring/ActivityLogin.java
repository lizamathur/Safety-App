package com.example.lizamathur.safetyring;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Iterator;

public class ActivityLogin extends AppCompatActivity {
    EditText inputNumber, inputPass;
    private Session session;
    Globals loggednumber = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);

        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);


        inputNumber = (EditText) findViewById(R.id.LNumber);
        inputPass = (EditText) findViewById(R.id.LPass);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }

        if (!session.loggedIn().equals("")) {
            loggednumber.setnum(session.loggedIn());
            Intent alwaysLoggedIn = new Intent(ActivityLogin.this, ActivityAlert.class);
            startActivity(alwaysLoggedIn);
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                } else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                break;
            case 2:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
                    }
                }
                break;
        }
    }
    public void changeActivityToRegister(View v) {
        Intent intentToActivityRegister = new Intent(ActivityLogin.this, ActivityRegister.class);
        startActivity(intentToActivityRegister);
        finish();

    }

    public void changeActivityToAlert(View v) {
        if (session.loggedIn().equals("")) {
            final String mobile = inputNumber.getText().toString();
            final String password = inputPass.getText().toString();

            if (mobile.equals("") || password.equals("")) {
                Toast.makeText(ActivityLogin.this, "Mobile and Password Required", Toast.LENGTH_SHORT).show();
            } else {

                DatabaseReference logInReference= FirebaseDatabase.getInstance().getReference();
                logInReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot snapshotbackup=null;
                        Boolean numberFound=false;
                        for (DataSnapshot snapshot : dataSnapshot.child("usercontacts").getChildren()) {
                            String numbersInDb = snapshot.getKey();
                            if (numbersInDb.equals(mobile)) {
                                snapshotbackup=snapshot;
                                numberFound = true;
                                break;
                            }
                        }
                        if(numberFound) {
                            DataSnapshot snapshotForPassword=snapshotbackup.child("3");
                            String passwordInDb= (String) snapshotForPassword.getValue();
                            if (passwordInDb.equals(password)){
                                session.setLoggedIn(mobile);
                                loggednumber.setnum(mobile);

                                Intent loginToAlert= new Intent(ActivityLogin.this,ActivityAlert.class);
                                startActivity(loginToAlert);
                                finish();
                            }else
                                Toast.makeText(ActivityLogin.this, "Please check Mobile/Password!", Toast.LENGTH_SHORT).show();

                        }else
                            Toast.makeText(ActivityLogin.this, "Please Register in the App!", Toast.LENGTH_SHORT).show();

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ActivityLogin.this, "Some error Occured. Please try again later!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

    }
}







