package com.example.YI;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CalendarActivity extends AppCompatActivity {
    TextView CalendarText;//时间文本视图
    DatePickerDialog datePickerDialog;//时间选择器
    StringBuffer stringBuffer;
    Button ButtonChoseTime, ButtonCurrentTime;//选择时间按钮，当前时间按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            String[] str_array = getResources().getStringArray(R.array.table_menu);
            Objects.requireNonNull(getSupportActionBar()).setTitle(str_array[4]);
        }//设置顶部返回箭头

        CalendarText = findViewById(R.id.textView_calendar);
        ButtonChoseTime = findViewById(R.id.button_chose_date);
        ButtonCurrentTime = findViewById(R.id.button_current_time);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            System.out.println("calender已销毁");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
