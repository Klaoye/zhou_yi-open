package com.example.YI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.base.bj.paysdk.domain.TrPayResult;
import com.base.bj.paysdk.listener.PayResultListener;
import com.base.bj.paysdk.utils.TrPay;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Global extends Intent {

    //音效方法
    protected SoundPool MySoundPool(Context context) {
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


    protected static class PayThread extends Thread {

        final String APPKEY = "45a9d41f49e546b487ba2486e10cb105";
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        public void run(Activity activity) {

            TrPay.getInstance(activity).initPaySdk(APPKEY, "donate");
            settings = activity.getSharedPreferences("data", MODE_PRIVATE);
            editor = settings.edit();

            TrPay.getInstance(activity).callAlipay("donate", "donate1", (long) 200,
                    "PayError", null, "klaoye@163.com",
                    new PayResultListener() {
                        @Override
                        public void onPayFinish(Context context, String s, int i, String s1, int i1, Long aLong, String s2) {
                            if (i == TrPayResult.RESULT_CODE_SUCC.getId()) {
                                editor.putBoolean("is_donated", true).apply();

                            } else if (i == TrPayResult.RESULT_CODE_FAIL.getId()) {
                                editor.putBoolean("is_donated", false).apply();

                            }
                        }
                    });
        }
    }

}
