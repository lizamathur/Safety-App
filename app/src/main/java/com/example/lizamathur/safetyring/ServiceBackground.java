package com.example.lizamathur.safetyring;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ServiceBackground extends IntentService {

    Geocoder geocoder;
    List<Address> addresses;
    LocationManager locationManager;
    String fullAddress;
    Location location;
    Globals rbclick = Globals.getInstance();
    Globals ton = Globals.getInstance();
    Globals setrestart = Globals.getInstance();
    Globals loggednumber = Globals.getInstance();
    CountDownTimer countDownTimerBetweenCalls;
    CountDownTimer timerToDelayCall;
    private static final int NOTIFICATION_ID = 1001;
    private int mRunningJobs = 0;
    DatabaseReference databaseBackground= FirebaseDatabase.getInstance().getReference();
    //CountDownTimer countDownTimer2;


    private boolean mIsForeground = false;

    public ServiceBackground() {
        super("ServiceBackground");
    }

    @Override
    protected void onHandleIntent(Intent intent) {    }


    @Override
    public int onStartCommand(final Intent intentserv, int flags, int startId) {
        func();
        return START_STICKY;
    }

    private void func() {

        //setrestart.setres(0);
        //countDownTimer4.cancel();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) {
            final Intent intentGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intentGps);
        }
        if (ActivityCompat.checkSelfPermission(ServiceBackground.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ServiceBackground.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //rbclick.setrb(0);
        fullAddress = "Location Unknown";

        if (location != null) {
            double latti = location.getLatitude();
            double longi = location.getLongitude();
            geocoder = new Geocoder(ServiceBackground.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latti, longi, 1);
                String address = addresses.get(0).getAddressLine(0);
                String area = addresses.get(0).getLocality();
                String city = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalcode = addresses.get(0).getPostalCode();
                fullAddress = address + ", " + area + ", " + city + ", " + country + ", " + postalcode;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        startForegroundIfNeeded();
        try {
            sendAlert();
        } catch (Exception e) {}
        //rbclick.setrb(1);

        countDownTimerBetweenCalls = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisFinished) {
                ton.setton(1);
            }
            @Override
            public void onFinish() {
                //if (ton.getton() == 1) {// && rbclick.getrb() == 1) {
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v1.vibrate(1000);
                    ton.setton(0);
                    //setrestart.setres(0);
                    func();
                //}
            }
        };
    }


    public void sendAlert(){

        final String[] numberToCall = {""};
        final int[] finalA = new int[1];

        databaseBackground.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final Handler handler1 = new Handler();
                final long noOfChildren=dataSnapshot.child("usercontacts").child(loggednumber.getnum()).getChildrenCount();
                final SmsManager smsManager = SmsManager.getDefault();
                for (int a = 1; a<=noOfChildren; a++){

                    final DataSnapshot snapshotOfAllDetails = dataSnapshot.child("usercontacts").child(loggednumber.getnum()).child(String.valueOf(a-1));
                    final long keyIterated=Long.parseLong(snapshotOfAllDetails.getKey());
                    finalA[0] =a;

                    handler1.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            if (keyIterated>=6 && keyIterated%3==0){
                                Toast.makeText(ServiceBackground.this, "Sending to: "+snapshotOfAllDetails.getValue()+" ; Message : "+fullAddress, Toast.LENGTH_SHORT).show();
                                /*smsManager.sendTextMessage(String.valueOf(snapshotOfAllDetails.getValue()), null, "HELP ME!\n" +
                                        "My Current Location: " + fullAddress, null, null);*/
                                if (keyIterated==noOfChildren-1) {

                                    timerToDelayCall=new CountDownTimer(2000,1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {                                        }

                                        @Override
                                        public void onFinish() {
                                            Intent intentcall = new Intent(Intent.ACTION_CALL);
                                            intentcall.setData(Uri.parse("tel:"+numberToCall[0]));
                                            if (ActivityCompat.checkSelfPermission(ServiceBackground.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                //ActivityCompat.requestPermissions(ServiceBackground.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                                            }
                                            Intent newIntent = Intent.createChooser(intentcall, "Complete With...");
                                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(newIntent);

                                            Toast.makeText(ServiceBackground.this, "Calling: "+numberToCall[0], Toast.LENGTH_SHORT).show();
                                            countDownTimerBetweenCalls.start();
                                        }
                                    }.start();
                                }
                            }

                            if (keyIterated==6){
                                numberToCall[0] = (String) snapshotOfAllDetails.getValue();
                            }

                        }
                    }, 500 * a);

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForegroundIfAllDone();
        if (countDownTimerBetweenCalls!=null && timerToDelayCall!=null) {
            countDownTimerBetweenCalls.cancel();
            timerToDelayCall.cancel();
        }
        stopSelf();
        /*if (setrestart.getres()==1 && !loggednumber.getnum().equals("")) {
            Toast.makeText(ServiceBackground.this, "Service startinggg", Toast.LENGTH_SHORT).show();
            setrestart.setres(0);
            startService(new Intent(this, ServiceBackground.class));
        }*/
    }

    public void startForegroundIfNeeded(){

        if(!mIsForeground){

            Notification notificationCompat = createBuiderNotification().build();
            startForeground(NOTIFICATION_ID,notificationCompat);
            mIsForeground = true;

        }
    }

    private void stopForegroundIfAllDone(){

        if(mRunningJobs == 0 &&  mIsForeground){
            stopForeground(true);
            mIsForeground = false;
        }
    }
    private NotificationCompat.Builder createBuiderNotification(){
        return  new NotificationCompat.Builder(this)
                .setContentTitle("Notification from Avrik")
                .setContentText("Alerting Contacts. Abort process to remove notification!")
                .setSmallIcon(R.mipmap.ic_launcher12);
    }
}
