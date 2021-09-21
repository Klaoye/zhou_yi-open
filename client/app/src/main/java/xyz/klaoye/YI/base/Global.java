/**
 * @author klaoye
 * @since jdk 1.8
 */
package xyz.klaoye.YI.base;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

import xyz.klaoye.YI.R;

public class Global {

    /**
     * @param context 基于上下文调用 .
     */
    public static SoundPool MySoundPool(Context context) {
        HashMap<Integer, Integer> voiceID = new HashMap<Integer, Integer>();//音频池列表
        SoundPool soundPool_table;//音频池
        SoundPool.Builder builder_soundPool_table;//音频池构建器
        AudioAttributes.Builder attr_builder;//音频流构建器

        //构建音频池
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder_soundPool_table = new SoundPool.Builder();
            builder_soundPool_table.setMaxStreams(3);//最多3个音频同时播放
            attr_builder = new AudioAttributes.Builder();
            attr_builder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//音乐模式
            builder_soundPool_table.setAudioAttributes(attr_builder.build());
            soundPool_table = builder_soundPool_table.build();
        } else {
            soundPool_table = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        //在音频池构建音频列表
        voiceID.put(1, soundPool_table.load(context, R.raw.book, 1));
        voiceID.put(2, soundPool_table.load(context, R.raw.water, 1));
        voiceID.put(3, soundPool_table.load(context, R.raw.guqin, 1));
        voiceID.put(4, soundPool_table.load(context, R.raw.guqin2, 1));

        return soundPool_table;
    }

}
