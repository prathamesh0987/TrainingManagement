package com.svrinfoteh.trainingmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.svrinfoteh.trainingmanagement.pojo.RevisionPoints;

public class ProvideAlertDialogInterface {

    public void setReason(final String point, final DatabaseReference revisionReference, final Context context) {
        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        final AlertDialog.Builder reasonDialog=new AlertDialog.Builder(context);
        final EditText giveReason=new EditText(context);
        reasonDialog.setView(giveReason);
        reasonDialog.setMessage("Please provide a valid reason");
        reasonDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reason=giveReason.getText().toString();
                RevisionPoints revisionPoints=new RevisionPoints(point,reason);
                revisionReference.child(point).setValue(revisionPoints);
            }
        });

        alertDialogBuilder.setMessage("Do you want to revise this topic");
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reasonDialog.show();
            }
        });

        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }

    public void removeReason(String point, DatabaseReference revisionReference) {
        revisionReference.child(point).removeValue();
    }
}
