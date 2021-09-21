/**
 * @author klaoye
 * @since jdk 1.8
 */
package xyz.klaoye.YI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.File;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends Activity {
    Intent Table;
    int open_screen_time = 3;
    SharedPreferences settings;//共享存储库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("data", MODE_PRIVATE);//获取名为data的共享存储库，仅限本软件修改和读取

        String infilePath = getFilesDir() + "/NoteData/";
        File infile = new File(infilePath);
        if (!infile.exists()) {
            infile.mkdir();
            Log.w("FileCreate: ", "文件夹 " + infilePath + " 已输出");
        }

        open_screen_time = settings.getInt("open_screen_time", 3);

        //GIF模块
        GifImageView GifIA1 = findViewById(R.id.GIFA1);
        GifDrawable GifDA1 = (GifDrawable) GifIA1.getDrawable();
        GifDA1.start();
        //延迟计时模块
        Handler mHandler = new Handler();
        Runnable mRunnable = this::finish;
        mHandler.postDelayed(mRunnable, open_screen_time * 1000L);
        Table = new Intent(MainActivity.this, TableActivity.class);
    }

    @Override
    public void finish() {
        startActivity(Table);
        super.finish();
    }

}

