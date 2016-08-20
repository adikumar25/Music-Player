package com.hp.example.aditya.musicsam;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    ListView lv;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private MediaPlayer mp = new MediaPlayer();
    private ImageButton btnPlaylist;
    private ImageView coverart;
    private Handler mHandler = new Handler();;
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    SharedPreferences sharedpreferences;
    NotificationManager manager;
    Notification mynotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        coverart=(ImageView)findViewById(R.id.cover);
        //lv=(ListView) findViewById(R.id.list);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        songManager = new SongsManager();
        utils = new Utilities();

        sharedpreferences = getSharedPreferences("Music", Context.MODE_PRIVATE);
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        songsList = songManager.songsList;

        int id=sharedpreferences.getInt("id",-1);
        int time=sharedpreferences.getInt("dur",0);

        if(id!=-1)
        {
            playSong(id);
            mp.seekTo(time);
            mp.pause();
        }
        else
        {
            playSong(0);
            mp.pause();
        }

        //playSong(0);
        //mp.pause();
        btnPlay.setImageResource(R.drawable.play1);



        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        manager.cancel(11);
                        btnPlay.setImageResource(R.drawable.play1);
                    }
                }else{

                    if(mp!=null){
                        mp.start();
                        setMynotification(songsList.get(currentSongIndex).get("songTitle"));
                        btnPlay.setImageResource(R.drawable.pause1);
                    }
                }

            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int currentPosition = mp.getCurrentPosition();

                if(currentPosition + seekForwardTime <= mp.getDuration()){

                    mp.seekTo(currentPosition + seekForwardTime);
                }else{

                    mp.seekTo(mp.getDuration());
                }
            }
        });


        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int currentPosition = mp.getCurrentPosition();

                if(currentPosition - seekBackwardTime >= 0){

                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{

                    mp.seekTo(0);
                }

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(isShuffle)
                {
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex);
                }
                else
                {
                    if(currentSongIndex < (songsList.size() - 1)){
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    }else{

                        playSong(0);
                        currentSongIndex = 0;
                    }
                }



            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle)
                {
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex);
                }
                else
                {
                    if(currentSongIndex > 0){
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    }else{

                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                    }
                }


            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.repeat2);
                }else{

                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();

                    //isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.repeat1);
                   // btnShuffle.setImageResource(R.drawable.shuffle1);
                }
            }
        });


        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.shuffle2);
                }else{

                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();

                   // isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.shuffle1);
                   // btnRepeat.setImageResource(R.drawable.repeat1);
                }
            }
        });


        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });


    }







        /*final ArrayList<File> mySongs=findSongs(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size() ];
        for(int i=0;i<mySongs.size();i++){
            // toast(mySongs.get(i).getName().toString());
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        ArrayAdapter<String> adp=new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.textView,items);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position));

            }
        });*/

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");

            playSong(currentSongIndex);
        }

    }


    public void  playSong(int songIndex){

        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();

            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            setMynotification(songTitle);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("id",songIndex);
            editor.commit();

            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(songsList.get(songIndex).get("songPath"));

            byte [] data = mmr.getEmbeddedPicture();

            if(data != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                coverart.setImageBitmap(bitmap);
                coverart.setAdjustViewBounds(true);
                coverart.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            }
            else
            {
                coverart.setImageResource(R.drawable.music);
                coverart.setAdjustViewBounds(true);
                coverart.setLayoutParams(new LinearLayout.LayoutParams(300,300 ));
            }


            btnPlay.setImageResource(R.drawable.pause1);


            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);


            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration=0 ;
            long currentDuration=0;

            if(mp!=null)
            {
                totalDuration = mp.getDuration();
                currentDuration = mp.getCurrentPosition();
            }




            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));

            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));


            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

            songProgressBar.setProgress(progress);


            mHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);


        mp.seekTo(currentPosition);


        updateProgressBar();
    }


    @Override
    public void onCompletion(MediaPlayer arg0) {


        if(isRepeat){

            playSong(currentSongIndex);
        } else if(isShuffle){

            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{

            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{

                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("dur",mp.getCurrentPosition());
        editor.commit();
        manager.cancel(11);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

       // mp.release();
    }










    public ArrayList<File> findSongs(File root){
        ArrayList<File> al=new ArrayList<File>();
        File[] files=root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory()&& !singleFile.isHidden()){
                al.addAll(findSongs(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3")|| singleFile.getName().endsWith(".wav")){
                    al.add(singleFile);
                }
            }

        }
        return al;
    }


    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }


    public void setMynotification(String name)
    {
        Intent intent =new Intent("com.hp.example.aditya.notification.SECACTIVITY");
        PendingIntent pi=PendingIntent.getActivity(MainActivity.this,1,intent,0);
        Notification.Builder builder=new Notification.Builder(MainActivity.this);
        builder.setAutoCancel(false);
        builder.setTicker(name);
        builder.setContentTitle("Now Playing");
        builder.setContentText(name);
        builder.setSmallIcon(R.drawable.music);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        //builder.setSubText("This is Subttext");
        //builder.setNumber();
        builder.build();



        mynotification=builder.getNotification();
        manager.notify(11,mynotification);
    }


}
