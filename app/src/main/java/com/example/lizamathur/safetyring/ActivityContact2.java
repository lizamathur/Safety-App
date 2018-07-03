package com.example.lizamathur.safetyring;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityContact2 extends AppCompatActivity {
    EditText c2Name,c2Mail,c2Number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact2);
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
        c2Name=(EditText)findViewById(R.id.c2Name);
        c2Mail=(EditText)findViewById(R.id.c2Mail);
        c2Number=(EditText)findViewById(R.id.c2Number);
    }
    public void changeActivity23(View v) {
        Contacts contact = new Contacts();
        if (c2Name.getText().toString().isEmpty() || c2Mail.getText().toString().isEmpty() || c2Number.getText().toString().isEmpty())
            Toast.makeText(ActivityContact2.this, "All fields Required!", Toast.LENGTH_SHORT).show();
        else {
            int response = contact.checkContactValidity(c2Mail.getText().toString(), c2Number.getText().toString());
            if (response == 2)
                Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
            else if (response == 3)
                Toast.makeText(this, "Please Enter a Valid Email-Id", Toast.LENGTH_SHORT).show();
            else {
                contact.addToList(2,c2Name.getText().toString(), c2Mail.getText().toString(), c2Number.getText().toString());
                Intent c2toc3 = new Intent(ActivityContact2.this, ActivityContact3.class);
                startActivity(c2toc3);
                finish();
            }
        }
    }
}
