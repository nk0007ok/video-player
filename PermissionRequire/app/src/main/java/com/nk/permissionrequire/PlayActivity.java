package com.nk.permissionrequire;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayActivity extends AppCompatActivity implements GestureDetection.SimpleGestureListener {

    Context context;
    VideoView play_video;
    AudioManager audioManager;
    GestureDetection detector;
    int currentPosition;
    int currentVolume;
    MediaController mediaController;
    MediaMetadataRetriever retriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")));
        getSupportActionBar().setElevation(0);
        play_video = (VideoView) findViewById(R.id.play_video);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        detector = new GestureDetection(this, this);

        String video_data = getIntent().getStringExtra("video");
        getSupportActionBar().setTitle(video_data.toString().split(",>")[0]);

        Uri video = Uri.parse(video_data.toString().split(",>")[1]);
        play_video.setVideoURI(video);
        mediaController = new MediaController(context);

        retriever = new MediaMetadataRetriever();
        retriever.setDataSource("" + video_data.toString().split(",>")[1]);
        int ori = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        retriever.release();

        if (ori == 90) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        mediaController.setAnchorView(play_video);
        mediaController.setMediaPlayer(play_video);
        play_video.setMediaController(mediaController);

        play_video.requestFocus();
        play_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play_video.start();

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                    }
                });
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        // TODO Auto-generated method stub

        switch (direction) {

            case GestureDetection.SWIPE_LEFT:

                currentPosition = play_video.getCurrentPosition();
                currentPosition = play_video.getCurrentPosition() + 5000;
                play_video.seekTo(currentPosition);
                break;

            case GestureDetection.SWIPE_RIGHT:

                currentPosition = play_video.getCurrentPosition();
                currentPosition = play_video.getCurrentPosition() - 5000;
                play_video.seekTo(currentPosition);
                break;

            case GestureDetection.SWIPE_DOWN:

                currentVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume - 1, 0);
                break;
            case GestureDetection.SWIPE_UP:

                currentVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume + 1, 0);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}