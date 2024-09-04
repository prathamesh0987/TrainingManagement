package com.svrinfoteh.trainingmanagement;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.holder.MessageHolder;
import com.svrinfoteh.trainingmanagement.pojo.ChatMessage;

import java.util.Date;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private RecyclerView chatRecyclerView;
    private EditText messageBody;
    private FloatingActionButton sendMessage;

    private DatabaseReference chatReference;

    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_chat, container, false);
        String subject=getArguments().getString("subject");
        init(rootView);
        chatReference= FirebaseReference.getFirebaseDatabaseReference().child("chats").child(subject);
        chatReference.keepSynced(true);
        FirebaseRecyclerOptions<ChatMessage> chatMessageFirebaseRecyclerOptions=new FirebaseRecyclerOptions
                .Builder<ChatMessage>()
                .setQuery(chatReference,ChatMessage.class)
                .build();

        final FirebaseRecyclerAdapter<ChatMessage,MessageHolder> adapter=
                new FirebaseRecyclerAdapter<ChatMessage, MessageHolder>(chatMessageFirebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull ChatMessage model) {

                        holder.setMessage(model.getMessage());
                        holder.setTime(DateFormat.format("HH:mm",model.getDateTime()));
                        holder.setUser(model.getUser());

                    }

                    @NonNull
                    @Override
                    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_row,parent,false);
                        return new MessageHolder(view);
                    }
                };

        adapter.startListening();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messageBody.getText().toString();
                String email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
                ChatMessage chatMessage=new ChatMessage(message,email,new Date().getTime());
                chatReference.push().setValue(chatMessage);
                messageBody.setText("");
            }
        });
    }

    private void init(View rootView) {
        chatRecyclerView=rootView.findViewById(R.id.chatView);
        sendMessage=rootView.findViewById(R.id.sendMessage);
        messageBody=rootView.findViewById(R.id.typeMessage);
    }
}