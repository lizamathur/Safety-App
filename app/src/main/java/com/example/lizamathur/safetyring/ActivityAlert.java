package com.example.lizamathur.safetyring;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityAlert extends AppCompatActivity {
    Button roundbutton,logout;
    TextView textViewLoggedIn;
    Globals rbclick=Globals.getInstance();
    Globals ton=Globals.getInstance();
    Globals setrestart=Globals.getInstance();
    private Session session;

    DatabaseReference databaseAlert=FirebaseDatabase.getInstance().getReference();
    Intent intentForBackground;
    Globals loggednumber=Globals.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        rbclick.setrb(0);
        ton.setton(0);
        session=new Session(this);
        if(session.loggedIn().equals(""))
        {
            logout();
        }
        logout=(Button)findViewById(R.id.logout);
        textViewLoggedIn=(TextView)findViewById(R.id.textViewLoggedIn);
        textViewLoggedIn.setText(loggednumber.getnum());
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);


        AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }

        }
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) {
            final Intent intentGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intentGps);
        }
        if (ActivityCompat.checkSelfPermission(ActivityAlert.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityAlert.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        roundbutton = (Button) findViewById(R.id.roundbutton);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        if(isMyServiceRunning(ServiceBackground.class)){
            rbclick.setrb(1);
            ton.setton(1);
            roundbutton.setTextSize(30);
            roundbutton.setText("Abort");
            roundbutton.setEnabled(true);
        }
        try {
            intentForBackground=new Intent(ActivityAlert.this,ServiceBackground.class);
        }catch (Exception e) {
            e.printStackTrace();
        }



        dotasks();
        //roundbutton.performClick();
    }

    CountDownTimer countDownTimerOf20Seconds;
    private String m_Text = "";

    private void dotasks() {

        roundbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //If the round button is clicked to start sending alerts
                if (rbclick.getrb()==0) {
                    rbclick.setrb(1);

                    String ctime = "20";
                    if (!ctime.equalsIgnoreCase("")) {
                        int sec = Integer.valueOf(ctime);
                        countDownTimerOf20Seconds = new CountDownTimer(sec * 1000, 1000) {
                            @Override
                            public void onTick(long millis) {
                                //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                //v.vibrate(500);
                                roundbutton.setTextSize(60);
                                roundbutton.setText("" + (int) (millis / 1000));
                            }


                            @Override
                            public void onFinish() {
                                //setrestart.setres(0);
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(500);
                                stopService(intentForBackground);
            /*EXTRA*/           rbclick.setrb(1);
                                startService(intentForBackground);
                                roundbutton.setTextSize(30);
                                roundbutton.setText("Abort");
                                roundbutton.setEnabled(true);
                                if (ActivityCompat.checkSelfPermission(ActivityAlert.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ActivityAlert.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                                }

                            }
                        }.start();
                    }
                }else { //either "ALERT NOW" needed or alerting contacts needs to be stopped

                    AlertDialog.Builder builder=new AlertDialog.Builder(ActivityAlert.this);
                    builder.setTitle("Enter PIN to abort!");
                    builder.setMessage("PIN not required for \"Alert Now\"");
                    final EditText input = new EditText(ActivityAlert.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                    builder.setView(input);


                    builder.setPositiveButton("Abort", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();

                            databaseAlert.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    DataSnapshot snapshot = dataSnapshot.child("usercontacts").child(loggednumber.getnum()).child("3");
                                    String passwordInDb= (String) snapshot.getValue();
                                    if (m_Text.equals(passwordInDb) && isMyServiceRunning(ServiceBackground.class)){
                                        //setrestart.setres(0);
                                        stopService(intentForBackground);
                                        rbclick.setrb(0);
                                        roundbutton.setTextSize(30);
                                        if (countDownTimerOf20Seconds != null)
                                            countDownTimerOf20Seconds.cancel();
                                        roundbutton.setText("Aborted");
                                        //ton.setton(0);
                                        roundbutton.setEnabled(false);
                                        Toast.makeText(ActivityAlert.this, "The process is aborted!", Toast.LENGTH_SHORT).show();
                                        CountDownTimer timerForAbortedToAlert = new CountDownTimer(5 * 1000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            @Override
                                            public void onFinish() {
                                                roundbutton.setTextSize(30);
                                                roundbutton.setText("Alert");
                                                roundbutton.setEnabled(true);
                                            }
                                        }.start();
                                    } else {
                                        if (rbclick.getrb() != 0 && isMyServiceRunning(ServiceBackground.class))
                                            Toast.makeText(ActivityAlert.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                        else {
                                            Toast.makeText(ActivityAlert.this, "No Ongoing Process To Abort!", Toast.LENGTH_SHORT).show();
                                            rbclick.setrb(0);
                                            if (countDownTimerOf20Seconds!=null)
                                                countDownTimerOf20Seconds.cancel();
                                            roundbutton.setTextSize(30);
                                            roundbutton.setText("Alert");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Alert Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopService(intentForBackground);
                            //rbclick.setrb(0);
                            rbclick.setrb(1);
                            //ton.setton(0);
                            //setrestart.setres(0);
                            if (countDownTimerOf20Seconds!=null)
                                countDownTimerOf20Seconds.cancel();
                            dialog.cancel();
                            countDownTimerOf20Seconds.onFinish();
                        }
                    });

                    builder.show();
                }

            }
        });

    }

    private void logout() {

        if (isMyServiceRunning(ServiceBackground.class))
            Toast.makeText(ActivityAlert.this, "Please abort the process first!", Toast.LENGTH_SHORT).show();
        else {
            if (countDownTimerOf20Seconds!=null)
                countDownTimerOf20Seconds.cancel();
            stopService(intentForBackground);
            session.setLoggedIn("");
            loggednumber.setnum("");
            Intent logoutint = new Intent(ActivityAlert.this, ActivityLogin.class);
            startActivity(logoutint);
            finish();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch (requestCode){
            case 1:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                }
                else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                break;
            case 2:
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
                    }
                }
                roundbutton.performClick();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting: {
                Toast.makeText(ActivityAlert.this, "Wait for the Update", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.search: {
                Toast.makeText(ActivityAlert.this, "Wait for the Update", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.version: {
                Toast.makeText(ActivityAlert.this, "v 1.0.2", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            moveTaskToBack(true);
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }
}
