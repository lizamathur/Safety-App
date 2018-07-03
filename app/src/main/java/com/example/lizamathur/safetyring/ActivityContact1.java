package com.example.lizamathur.safetyring;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityContact1 extends AppCompatActivity {
    EditText c1Name,c1Mail,c1Number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact1);
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
        c1Name=  (EditText)(findViewById(R.id.c1Name));
        c1Mail= (EditText)(findViewById(R.id.c1Mail));
        c1Number= (EditText)(findViewById(R.id.c1Number));
    }

    public void changeActivity12(View v) {

        Contacts contact = new Contacts();
        if (c1Name.getText().toString().isEmpty() || c1Mail.getText().toString().isEmpty() || c1Number.getText().toString().isEmpty())
            Toast.makeText(ActivityContact1.this, "All fields Required!", Toast.LENGTH_SHORT).show();
        else {
            int response = contact.checkContactValidity(c1Mail.getText().toString(), c1Number.getText().toString());
            if (response == 2)
                Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
            else if (response == 3)
                Toast.makeText(this, "Please Enter a Valid Email-Id", Toast.LENGTH_SHORT).show();
            else {
                contact.addToList(1,c1Name.getText().toString(), c1Mail.getText().toString(), c1Number.getText().toString());
                Intent c1toc2 = new Intent(ActivityContact1.this, ActivityContact2.class);
                startActivity(c1toc2);
                finish();
            }
        }
    }
}
