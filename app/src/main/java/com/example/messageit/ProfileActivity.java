package com.example.messageit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.InvalidObjectException;

public class ProfileActivity extends AppCompatActivity
{
    private String receiverUserId, senderUserId ,Current_State;

    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton, DeclineMessageRequestButton;

    private DatabaseReference UserRef, ChatRequestRef, ContactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");


        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();

        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button) findViewById(R.id.decline_message_request_button);

        Current_State = "new";


        RetrieveUserInfo();
    }

    private void RetrieveUserInfo()
    {
        UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ((snapshot.exists())  && (snapshot.hasChild("name")))
                {
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequests();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void ManageChatRequests()
    {


        ChatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.hasChild(receiverUserId))
                        {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                Current_State = "request_sent";
                                sendMessageRequestButton.setText("Cancel Request");
                            }
                            else if (request_type.equals("received"))
                            {
                                Current_State = "request_received";
                                sendMessageRequestButton.setText("Accept Chat Request");

                                DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);

                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            ContactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if (snapshot.hasChild(receiverUserId))
                                            {
                                                Current_State = "friends";
                                                sendMessageRequestButton.setText("Remove this Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if (!senderUserId.equals(receiverUserId))
        {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    sendMessageRequestButton.setEnabled(false);

                    if (Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if (Current_State.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if (Current_State.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                    if (Current_State.equals("friends"))
                    {
                        RemoveSpecificContact();
                    }
                }
            });
        }
        else
        {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact()
    {
        ContactsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest()
    {
        ContactsRef.child(senderUserId).child(receiverUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                ChatRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    ChatRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
                                                                                    sendMessageRequestButton.setText("Remove this Contact");

                                                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void CancelChatRequest()
    {
        ChatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                sendMessageRequestButton.setText("Send Message");


                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }



    private void SendChatRequest()
    {
        ChatRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "request_sent";
                                                sendMessageRequestButton.setText("Cancel Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}