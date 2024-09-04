package com.svrinfoteh.trainingmanagement.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.svrinfoteh.trainingmanagement.R;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;

public class EventHolder extends RecyclerView.ViewHolder {

    private TextView title,description,userLikes;
    private ImageView eventImage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceLikes;
    public ImageButton like;
    public EventHolder(View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.evetitle);
        userLikes=itemView.findViewById(R.id.like_count);
        description=itemView.findViewById(R.id.eventDescription);
        eventImage=itemView.findViewById(R.id.eventImage);
        like=itemView.findViewById(R.id.like_btn);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReferenceLikes= FirebaseReference.getFirebaseDatabaseReference().child("likes");
        databaseReferenceLikes.keepSynced(true);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    public void setEventImage(final String image,final Context context) {
        Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(eventImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(image).into(eventImage);
            }
        });
    }

    public void setLike(final String post_key) {
        databaseReferenceLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                    like.setImageResource(R.mipmap.ic_favorite_black_36dp);
                    Long count=dataSnapshot.child(post_key).getChildrenCount();
                    userLikes.setText(String.valueOf(count));
                } else {
                    like.setImageResource(R.mipmap.ic_favorite_border_black_36dp);
                    Long count=dataSnapshot.child(post_key).getChildrenCount();
                    userLikes.setText(String.valueOf(count));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
