package com.example.YI;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


public class MusicService extends Service {
    static boolean isPlay;//是否在播放
    MediaPlayer MusicPlayer;//播放器

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        MusicPlayer = MediaPlayer.create(this, R.raw.music);
        MusicPlayer.setLooping(true);//循环播放
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isPlay) {//isPlaying为否时
            MusicPlayer.start();
            isPlay = MusicPlayer.isPlaying();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MusicPlayer.stop();
        isPlay = MusicPlayer.isPlaying();
        MusicPlayer.release();
        super.onDestroy();
    }


}
