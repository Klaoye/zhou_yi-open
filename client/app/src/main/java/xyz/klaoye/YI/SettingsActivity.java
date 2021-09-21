/**
 * @author klaoye
 * @since jdk 1.8
 */
package xyz.klaoye.YI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    SharedPreferences settings;
    SharedPreferences.Editor settings_editor;
    Intent MusicService;//音乐服务
    SeekBar seekBarOpenScreenTime;
    ArrayList<Switch> switches = new ArrayList<>();
    private boolean is_play_music;//是否播放音乐
    private boolean is_play_sounds;//是否播放音效
    private boolean can_copy;
    int open_screen_time;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            String[] str_array = getResources().getStringArray(R.array.table_menu);
            Objects.requireNonNull(getSupportActionBar()).setTitle(str_array[2]);
        }//设置顶部返回箭头

        MusicService = new Intent(SettingsActivity.this, MusicService.class);

        switches.add(findViewById(R.id.switch_music));
        switches.add(findViewById(R.id.switch_sounds));
        switches.add(findViewById(R.id.switch_can_copy));


        seekBarOpenScreenTime = findViewById(R.id.seekBar_animation_time);

        settings = getSharedPreferences("data", Context.MODE_PRIVATE);
        settings_editor = settings.edit();

        is_play_music = settings.getBoolean("is_play_music", true);
        is_play_sounds = settings.getBoolean("is_play_sounds", true);
        open_screen_time = settings.getInt("open_screen_time", 3);
        can_copy = settings.getBoolean("can_copy", false);

        switches.get(0).setOnCheckedChangeListener(this);
        switches.get(1).setOnCheckedChangeListener(this);
        switches.get(2).setOnCheckedChangeListener(this);

        seekBarOpenScreenTime.setOnSeekBarChangeListener(this);
        seekBarOpenScreenTime.setProgress(open_screen_time);

        if (is_play_music) {//音乐开关
            switches.get(0).setChecked(true);
        } else {
            switches.get(0).setChecked(false);
        }
        if (is_play_sounds) {//音效开关
            switches.get(1).setChecked(true);
        } else {
            switches.get(1).setChecked(false);
        }
        if (can_copy) {
            switches.get(2).setChecked(true);
        } else {
            switches.get(2).setChecked(false);
        }

        switches.get(0).setEnabled(false);
        switches.get(0).setCursorVisible(false);
        switches.get(2).setEnabled(false);//“可复制”开关不可触发
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int view_id = 0;
        if (compoundButton.equals(switches.get(0))) {
            view_id = 0;
            settings_editor.putBoolean("is_play_music", b).apply();//开关控制音乐播放并写入数值
            is_play_music = settings.getBoolean("is_play_music", is_play_music);
            Log.w("switch music", "音效播放为：" + b);
        } else if (compoundButton.equals(switches.get(1))) {
            view_id = 1;
            settings_editor.putBoolean("is_play_sounds", b).apply();//开关控制音效写入数值
            is_play_sounds = settings.getBoolean("is_play_sounds", is_play_sounds);
            Log.w("switch sound", "音效播放为：" + b);
        } else if (compoundButton.equals(switches.get(2))) {
            view_id = 2;
            settings_editor.putBoolean("can_copy", b).apply();
            can_copy = settings.getBoolean("can_copy", false);
            Log.w("switch can_copy", "可否复制正文" + b);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (b) {
                switches.get(view_id).setThumbResource(R.drawable.switch_thumb_white);
            } else {
                switches.get(view_id).setThumbResource(R.drawable.switch_thumb_black);
            }
        }

    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.equals(seekBarOpenScreenTime)) {
            settings_editor.putInt("open_screen_time", i).apply();
            Toast.makeText(this, getString(R.string.time_changed, i), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
