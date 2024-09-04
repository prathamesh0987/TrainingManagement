package com.svrinfoteh.trainingmanagement;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.holder.EventHolder;
import com.svrinfoteh.trainingmanagement.pojo.Events;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Event extends Fragment {

    private RecyclerView displayEvent;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceLikes;
    private DatabaseReference databaseReferenceEvent;
    private String userStatus;
    private final String sharedPreferenceName="SHARED_PREFERENCE";
    private SharedPreferences sharedPreferences;
    private boolean likeValue=false;

    public Event() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_event, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init(rootView);
        displayEvent.setHasFixedSize(true);
        displayEvent.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        final FirebaseRecyclerOptions<Events> firebaseRecyclerOptions=new FirebaseRecyclerOptions
                .Builder<Events>()
                .setQuery(databaseReferenceEvent, Events.class)
                .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Events, EventHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final EventHolder holder, int position, @NonNull Events model) {
                        final String post_key=getRef(position).getKey();
                        holder.setTitle(model.getTitle());
                        holder.setDescription(model.getDescription());
                        holder.setEventImage(model.getImage(),getActivity().getApplicationContext());

                        holder.like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likeValue=true;
                                databaseReferenceLikes.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(likeValue) {
                                            if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                                databaseReferenceLikes.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                                likeValue=false;
                                            } else {
                                                databaseReferenceLikes.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).setValue("Liked");
                                                likeValue=false;
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        holder.setLike(post_key);
                    }

                    @Override
                    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View itemView = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.event_row, parent, false);
                        return new EventHolder(itemView);
                    }
                };

        displayEvent.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        setHasOptionsMenu(true);
        return rootView;
    }

    private void init(View view) {
        displayEvent=view.findViewById(R.id.displayEvent);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReferenceLikes= FirebaseReference.getFirebaseDatabaseReference().child("likes");
        databaseReferenceEvent=FirebaseReference.getFirebaseDatabaseReference().child("event");
        databaseReferenceLikes.keepSynced(true);
        databaseReferenceEvent.keepSynced(true);
        sharedPreferences=getActivity().getApplicationContext().getSharedPreferences(sharedPreferenceName,MODE_PRIVATE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPost:
                displayEvent.setVisibility(View.GONE);
                getFragmentManager().beginTransaction().replace(R.id.eventLayout,new EventPost()).commit();
                setMenuVisibility(false);
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }
}
