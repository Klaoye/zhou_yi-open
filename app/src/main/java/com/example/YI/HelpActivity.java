package com.example.YI;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class HelpActivity extends AppCompatActivity {

    ListView HelpList;//帮助界面列表
    AlertDialog HelpAlertDialog, ReadZhouYiAlertDialog;
    String[] string;
    View gifView;
    GifImageView HGIV;
    GifDrawable HGD;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        string = getResources().getStringArray(R.array.help_array);
        gifView = getLayoutInflater().inflate(R.layout.gif_view, null);
        HelpList = findViewById(R.id.help_list);

        HGIV = gifView.findViewById(R.id.GIF_help);
        HGD = (GifDrawable) HGIV.getDrawable();

        HelpAlertDialog = new AlertDialog.Builder(this)
                .setTitle(string[1])
                .setIcon(R.mipmap.ic_launcher)
                .setView(gifView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HGD.stop();
                        HGD.reset();
                    }
                })
                .create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.wood240));
        }//修改活动栏样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            String[] str_arry = getResources().getStringArray(R.array.table_menu);
            Objects.requireNonNull(getSupportActionBar()).setTitle(str_arry[1]);
        }//设置顶部返回箭头

        HelpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ReadZhouYiAlertDialog = funAlertDialog(string[position], getString(R.string.hou_to_read));
                        ReadZhouYiAlertDialog.show();
                        break;
                    case 1:
                        HelpAlertDialog.show();
                        HGD.start();
                        HGD.reset();
                        break;
                    case 2:

                    case 3:
                        Toast.makeText(HelpActivity.this, R.string.building, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            System.out.println("help已销毁");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected AlertDialog funAlertDialog(String TittleStr, String message) {
        AlertDialog.Builder ADbuilder = new AlertDialog.Builder(HelpActivity.this);
        AlertDialog alertDialog;
        ADbuilder.setIcon(R.mipmap.ic_launcher)
                .setTitle(TittleStr)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = ADbuilder.create();
        System.out.println("AlertDialog 输出");
        return alertDialog;
    }

}
