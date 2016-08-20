package com.hp.example.aditya.musicsam;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Aditya on 08-06-2016.
 */
public class MusicService extends Service {
    static MediaPlayer mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) getApplicationContext());


    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show();
       ;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Service Stopped",Toast.LENGTH_SHORT).show();

    }


}
