package com.example.messageit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RequestsFragment extends Fragment
{

    private View RequestsFragmentView;
    private RecyclerView myRequestsList;

    private DatabaseReference ChaRequestsRef, UsersRef;

    private FirebaseAuth mAuth;
    private String currentUserId;


    public RequestsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RequestsFragmentView =  inflater.inflate(R.layout.fragment_requests, container, false);

        ChaRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myRequestsList = (RecyclerView) RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestsFragmentView;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChaRequestsRef.child(currentUserId), Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model)
                    {
                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);


                        final String list_user_id = getRef(position).getKey();


                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if (snapshot.exists())
                                {
                                    String type = snapshot.getValue().toString();

                                    if (type.equals("received"))
                                    {
                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                    final String requestUserName = snapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = snapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;
                    }
                };

        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        Button AcceptButton, CancelButton;



        public RequestViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);

        }
    }
}