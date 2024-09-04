package com.svrinfoteh.trainingmanagement.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svrinfoteh.trainingmanagement.R;
import com.svrinfoteh.trainingmanagement.pojo.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    ArrayList<User> userList;

    public UserAdapter(ArrayList<User> userList) {
        this.userList = userList;
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        TextView name,contactno;
        public UserHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.userName);
            contactno=itemView.findViewById(R.id.contact);
        }
    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_row,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user=userList.get(position);
        StringBuffer stringBuffer=new StringBuffer("Name : ").append(user.getName());
        holder.name.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Contact No : ").append(user.getMobile());
        holder.contactno.setText(stringBuffer.toString());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
