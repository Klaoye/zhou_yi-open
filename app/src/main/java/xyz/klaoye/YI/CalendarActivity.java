/**
 * @author klaoye
 * @since jdk 1.8
 * 黄历查询活动
 */
package xyz.klaoye.YI;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import xyz.klaoye.YI.bean.CalendarTranslator;

public class CalendarActivity extends AppCompatActivity {
    DatePickerDialog datePickerDialog;//时间选择器
    ListView listViewCalendar;
    Adapter adapter;
    ArrayList<String> msgArrayList;
    Button buttonChoseTime, buttonCurrentTime;//选择时间按钮，当前时间按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String[] str_array = getResources().getStringArray(R.array.table_menu);
        Objects.requireNonNull(getSupportActionBar()).setTitle(str_array[4]);

        buttonChoseTime = findViewById(R.id.button_chose_date);
        buttonCurrentTime = findViewById(R.id.button_current_time);
        listViewCalendar = findViewById(R.id.list_view_calendar);
        msgArrayList = new ArrayList<>();


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, msgArrayList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        CalendarTranslator CT = new CalendarTranslator(this);
        Log.i("onCreate: ", Arrays.toString(CT.getGanZhi(calendar)));
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
