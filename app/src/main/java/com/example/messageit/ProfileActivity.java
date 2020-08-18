package com.example.messageit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity
{
    private String receiverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();

        Toast.makeText(this, "UserID: " + receiverUserId, Toast.LENGTH_SHORT).show();
    }

}