package com.svrinfoteh.trainingmanagement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.util.Date;

public class SubscriptionService extends Service {
    public SubscriptionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTrueTime(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Date date=getTrueTimeDate();
        String realDate= DateFormat.format("dd-MM-yyyy",date.getTime()).toString();
        String dueDate="07-06-2018";

        int realDateInt=Integer.parseInt(realDate.replaceAll("-",""));
        int dueDateInt=Integer.parseInt(dueDate.replaceAll("-",""));

        if(realDate.equals(dueDate) || realDateInt>=dueDateInt) {
            Toast.makeText(
                    getApplicationContext(),
                    "Subscription Expires MSG From Services",
                    Toast.LENGTH_LONG
            ).show();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void initTrueTime(Context context) {
        if(isNetworkConnected(context)) {
            if(!TrueTime.isInitialized()) {
                new InitilizeTrueTime(context).execute();
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }

    public Date getTrueTimeDate() {
        Date date=TrueTime.isInitialized() ? TrueTime.now():new Date();
        return date;
    }

    private static class InitilizeTrueTime extends AsyncTask<Void,Void,Void> {
        private Context context;
        public InitilizeTrueTime(Context context) {
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TrueTime.build()
                        .withSharedPreferences(context)
                        .withNtpHost("time.google.com")
                        .withLoggingEnabled(false)
                        .withConnectionTimeout(31_428)
                        .initialize();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception : ",e.getMessage());
            }

            return null;
        }
    }
}
