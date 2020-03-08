package com.example.YI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences settings;
    SharedPreferences.Editor settings_editor;
    Switch switch_play_music;//音乐开关
    Switch switch_play_sounds;//音效开关
    Switch switch_typeface;
    Intent MusicService;//音乐服务
    private boolean is_play_music;//是否播放音乐
    private boolean is_play_sounds;//是否播放音效
    private boolean use_typeface;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            String[] str_arry = getResources().getStringArray(R.array.table_menu);
            Objects.requireNonNull(getSupportActionBar()).setTitle(str_arry[2]);
        }//设置顶部返回箭头
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.wood240));
        }//修改活动栏样式

        MusicService = new Intent(SettingsActivity.this, MusicService.class);

        switch_play_music = findViewById(R.id.switch_music);
        switch_play_sounds = findViewById(R.id.switch_sounds);
        switch_typeface = findViewById(R.id.switch_type_face);

        settings = getSharedPreferences("data", Context.MODE_PRIVATE);
        settings_editor = settings.edit();

        is_play_music = settings.getBoolean("is_play_music", is_play_music);
        is_play_sounds = settings.getBoolean("is_play_sounds", is_play_sounds);
        use_typeface = settings.getBoolean("is_use_typeface", use_typeface);

        if (is_play_music) {//音乐开关
            switch_play_music.setChecked(true);
        } else {
            switch_play_music.setChecked(false);
        }
        if (is_play_sounds) {//音效开关
            switch_play_sounds.setChecked(true);
        } else {
            switch_play_sounds.setChecked(false);
        }
        if (use_typeface) {
            switch_typeface.setChecked(true);
        } else {
            switch_typeface.setChecked(false);
        }

        switch_play_music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch_play_music.setThumbResource(R.drawable.switch_thumb_white);
                    }
                    startService(MusicService);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch_play_music.setThumbResource(R.drawable.switch_thumb_black);
                    }
                    stopService(MusicService);
                }
                settings_editor.putBoolean("is_play_music", b).apply();//开关控制音乐播放并写入数值
                is_play_music = settings.getBoolean("is_play_music", is_play_music);
                System.out.println("音乐播放为：" + b);
            }
        });

        switch_play_sounds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch_play_sounds.setThumbResource(R.drawable.switch_thumb_white);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch_play_sounds.setThumbResource(R.drawable.switch_thumb_black);
                    }
                }
                settings_editor.putBoolean("is_play_sounds", b).apply();//开关控制音效写入数值
                is_play_sounds = settings.getBoolean("is_play_sounds", is_play_sounds);
                System.out.println("音效播放为：" + b);
            }
        });

        switch_typeface.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch_typeface.setThumbResource(R.drawable.switch_thumb_white);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch_typeface.setThumbResource(R.drawable.switch_thumb_black);
                    }
                }
                settings_editor.putBoolean("is_use_typeface", b).apply();
                use_typeface = settings.getBoolean("is_use_typeface", use_typeface);
                System.out.println("使用内置字体" + b);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            System.out.println("setting已销毁");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
