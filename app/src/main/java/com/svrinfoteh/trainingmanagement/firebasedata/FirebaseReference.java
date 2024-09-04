package com.svrinfoteh.trainingmanagement.firebasedata;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseReference {

    public  static DatabaseReference getFirebaseDatabaseReference() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance("https://svrdemo.firebaseio.com/").getReference();
        return databaseReference;
    }

    public static StorageReference getFirebaseStorageReference() {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("SVR InfoTech");
        return storageReference;
    }
}
