package com.example.messageit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class ChatActivity extends AppCompatActivity
{

    private String messageReceiverID, messageReceiverName, messageSenderID;
    private TextView userName, userLastSeen;

    private Toolbar ChatToolBar;
    private FirebaseAuth mAuth;

    private ImageButton SendMessageButton;
    private EditText MessageInputText;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();

        InitializeControllers();

        userName.setText(messageReceiverName);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMessage();
            }
        });

    }

    private void InitializeControllers()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);
        getSupportActionBar().setTitle(null);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View actionBarView = layoutInflater.inflate(R.layout.custon_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);
    }


    private void SendMessage()
    {
        String MessageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(MessageText))
        {
            Toast.makeText(this, "Please Type a Message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

        }
    }
}