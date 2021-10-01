package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {


    Button btnPlay,btnNext,btnPrev,btFf,btnFr;
    TextView txtName ,txtStart ,txtStop;
    SeekBar seekBar;
    BlobVisualizer visualizer;
//        ImageView imageView;
    String sName;
    public static final String EXTRA_NAME ="song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList <File>mySongs;

    Thread updateSeekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (visualizer!=null){
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);







        visualizer =findViewById(R.id.blast);
        btnPrev =findViewById(R.id.previousBtn);
        btnNext =findViewById(R.id.nextBtn);
        btnPlay =findViewById(R.id.playBtn);
        btFf =findViewById(R.id.fastForwardBtn);
        btnFr =findViewById(R.id.fastRewindBtn);
//        imageView=findViewById(R.id.imgView);
        seekBar = findViewById(R.id.seek_bar);

        txtName = findViewById(R.id.text_1);
        txtStart= findViewById(R.id.textStart);
        txtStop= findViewById(R.id.textStop);
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String st = intent.getStringExtra("songName");
        position =bundle.getInt("pos",0);
        txtName.setSelected(true);
        Uri uri =Uri.parse(mySongs.get(position).toString());
        st = mySongs.get(position).getName();
        txtName.setText(st.replace(".mp3",""));

        mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration =mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
       // seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
      //  seekBar.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
            String endTime = createTime(mediaPlayer.getDuration());
            txtStop.setText(endTime);

            final Handler handler = new Handler();
            final  int delay =1000;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String currentTime = createTime(mediaPlayer.getCurrentPosition());
                    txtStart.setText(currentTime);
                    handler.postDelayed(this,delay);

                }
            },delay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();

                }
                else {
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(),u);
                sName = mySongs.get(position).getName();
                txtName.setText(sName.replace(".mp3",""));
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
//                startAnimation(imageView);
                int  audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId!=-1)
                {
                    visualizer.setAudioSessionId(audioSessionId);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(),u);
                sName = mySongs.get(position).getName();
                txtName.setText(sName.replace(".mp3",""));
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
//                startAnimation(imageView);
                int  audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId!=-1)
                {
                    visualizer.setAudioSessionId(audioSessionId);
                }

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();



            }
        });

        int  audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId!=-1)
        {
            visualizer.setAudioSessionId(audioSessionId);
        }

        btFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }

            }
        });

        btnFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }

            }
        });

    }

//    public void startAnimation(ImageView imageView){
//        ObjectAnimator animator = ObjectAnimator.ofFloat(this.imageView,"rotation",0f,360f);
//        animator.setDuration(1000);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animator);
//        animatorSet.start();
//
//
//
//    }
    public String createTime(int duration){
        String time= "";
        int min =duration/1000/60;
        int sec =duration/1000%60;
        time+=min+":";
            if (sec<10){
                time+="0";


            }
                time+=sec;
            return time;


    }

    public void onGoBack(View view) {
        onBackPressed();
    }
}