package xyz.klaoye.YI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xyz.klaoye.YI.bean.AlertDialogFactory;
import xyz.klaoye.YI.bean.Global;
import xyz.klaoye.YI.bean.Tools;
import xyz.klaoye.YI.databases.Operator;

public class TableActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Button BtnSearch;//检索键
    Spinner spinnerGua8Up;//下拉框上卦
    Spinner spinnerGua8Dn;//下拉框下卦
    Spinner spinnerGua64;//下拉框六十四卦
    SpinnerAdapter adapterGua8;//八卦适配器
    SpinnerAdapter adapterGua64;//六十四卦适配器
    SharedPreferences settings;//共享存储库
    SharedPreferences.Editor editor;//共享存储库编辑器

    SoundPool soundPool_table;//音频池
    HashMap<String, String> guaMap;

    Intent MusicService;//音乐服务
    Intent SettingsActivity;//跳转至设置界面
    Intent NoteActivity;
    Intent HelpActivity;
    Intent CalendarActivity;
    String[] menu_id;//菜单列表
    boolean play_music;//是否播放音乐
    boolean music_setting;//音乐是否有初始设置
    boolean play_sounds;//是否播放音效
    boolean sounds_setting;//音效是否有初始设置
    boolean first_use;
    boolean can_copy;
    AlertDialog aboutAlertDialog;
    TextView searchTextView;//文字界面
    //Typeface typeface;//字体
    //Switch modelSwitch;//模式开关
    short up_gua8_id;//上卦
    short dn_gua8_id;//下卦
    short gua64_id;//六十四卦
    boolean model;//模式
    long exit_time = 0;
    boolean press_again = false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        //实例化Intent
        SettingsActivity = new Intent(TableActivity.this, SettingsActivity.class);
        MusicService = new Intent(TableActivity.this, MusicService.class);
        NoteActivity = new Intent(TableActivity.this, NoteActivity.class);
        HelpActivity = new Intent(TableActivity.this, HelpActivity.class);
        CalendarActivity = new Intent(TableActivity.this, CalendarActivity.class);

        menu_id = getResources().getStringArray(R.array.table_menu);//菜单列表数组

        soundPool_table = Global.MySoundPool(TableActivity.this);

        settings = getSharedPreferences("data", MODE_PRIVATE);//获取名为data的共享存储库，仅限本软件修改和读取
        editor = settings.edit();//共享存储库实例化

        //获取存储库中值
        music_setting = settings.getBoolean("music_setting", true);
        sounds_setting = settings.getBoolean("sounds_setting", true);
        first_use = settings.getBoolean("first_use", true);
        can_copy = settings.getBoolean("can_copy", false);

        AlertDialogFactory dialogFactory = new AlertDialogFactory(this);

        if (music_setting) {
            editor.putBoolean("is_play_music", true).apply();
            play_music = settings.getBoolean("is_play_music", true);
            editor.putBoolean("music_setting", false).apply();
            music_setting = false;
            System.out.println("音乐初始化成功");
        }//判断音乐设置是否有过改动

        if (sounds_setting) {
            editor.putBoolean("is_play_sounds", true).apply();
            play_sounds = settings.getBoolean("is_play_sounds", true);
            editor.putBoolean("sounds_setting", false).apply();
            sounds_setting = false;
            System.out.println("音效初始化成功");
        }//判断音效设置是否有过改动

        play_music = settings.getBoolean("is_play_music", true);
        play_sounds = settings.getBoolean("is_play_sounds", true);

        if (play_music) {//是否启动音乐服务
            startService(MusicService);
        } else {
            stopService(MusicService);
        }

        model = false;//模式初始值
        //typeface = Typeface.createFromAsset(getAssets(), "fonts/Song.otf");//注册字体

        //********列表带图片***********
        spinnerGua8Up = findViewById(R.id.spinner_gua_8_up);//上卦
        spinnerGua8Dn = findViewById(R.id.spinner_gua_8_dn);//下挂
        spinnerGua64 = findViewById(R.id.spinner_gua_64);//六十四卦

        TypedArray imageID = getResources().obtainTypedArray(R.array.table_menu_drawable);
        //图片ID数组

        String[] gua_8_texts = getResources().getStringArray(R.array.gua_8);//列表文字数组-八卦
        String[] gua_64_texts = getResources().getStringArray(R.array.gua_64);//六十四卦

        ArrayList<Map<String, Object>> list_gua_8 = new ArrayList<>();//创建八卦文字列表
        //遍历图片及文字
        for (int i = 0; i < imageID.length(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", imageID.getResourceId(i, 0));
            map.put("text", gua_8_texts[i]);
            list_gua_8.add(map);
        }
        //创建六十四卦文字列表
        ArrayList<Map<String, String>> list_gua_64 = new ArrayList<>();
        for (String s : gua_64_texts) {
            Map<String, String> map = new HashMap<>();
            map.put("text", s);
            list_gua_64.add(map);
        }
        //创建八卦适配器
        adapterGua8 = new SimpleAdapter(this, list_gua_8, R.layout.gua_item_8,
                new String[]{"text", "image"}, new int[]{R.id.textView_gua_8, R.id.imageView_gua});
        //创建六十四卦适配器
        adapterGua64 = new SimpleAdapter(this, list_gua_64, R.layout.gua_item_64,
                new String[]{"text"}, new int[]{R.id.textView_gua_64});
        //上卦装载适配器
        spinnerGua8Up.setAdapter(adapterGua8);
        //下挂装载适配器
        spinnerGua8Dn.setAdapter(adapterGua8);
        //六十四卦装载适配器
        spinnerGua64.setAdapter(adapterGua64);
        //*******************************

        searchTextView = findViewById(R.id.textView_search);//文本框
        searchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        spinnerGua8Up.setOnItemSelectedListener(this);
        spinnerGua8Dn.setOnItemSelectedListener(this);
        spinnerGua64.setOnItemSelectedListener(this);
        if (can_copy) {
            searchTextView.setTextIsSelectable(true);//复制文本框文字
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        } else {
            searchTextView.setTextIsSelectable(false);
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        }

        //开发者信息
        aboutAlertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.company)
                .setIcon(R.drawable.company)
                .setMessage(getString(R.string.developer_information) + getString(R.string.suggestion) + getString(R.string.donate_information))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                })
                .setNegativeButton(R.string.donate, (dialog, which) -> {
                })
                .create();

        if (first_use) {
            //第一次使用隐私政策宣传
            AlertDialog privacyPolicyAlertdialog = dialogFactory.getAgreeDialog(
                    getString(R.string.privacy_policy),
                    R.mipmap.ic_launcher,
                    getString(R.string.privacy_policy_context),
                    ((dialog, which) -> editor.putBoolean("first_use", false).apply()),
                    ((dialog, which) -> System.exit(0))
            );

            //第一次使用引导
            AlertDialog firstUseAlertdialog = dialogFactory.getAgreeDialog(
                    getString(R.string.first_use_title),
                    R.mipmap.ic_launcher,
                    getString(R.string.first_wse_message),
                    ((dialog, which) -> startActivity(HelpActivity)),
                    null
            );
            first_use = settings.getBoolean("first_use", first_use);
            firstUseAlertdialog.show();
            privacyPolicyAlertdialog.show();
        }
        guaMap = getGuaMap();

    }

    //点击下拉框
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int parentViewID = parent.getId();
        System.out.println(parentViewID);
        if (parentViewID == spinnerGua8Up.getId() || parentViewID == spinnerGua8Dn.getId()) {
            if (parentViewID == spinnerGua8Up.getId()) {
                up_gua8_id = (short) position;
                dn_gua8_id = (short) spinnerGua8Dn.getSelectedItemPosition();
            } else {
                up_gua8_id = (short) spinnerGua8Up.getSelectedItemPosition();
                dn_gua8_id = (short) position;
            }
            int position_max = up_gua8_id * 8 + dn_gua8_id;//六十四卦
            spinnerGua64.setSelection(position_max);//跳转卦名
            findGua(up_gua8_id, dn_gua8_id);
        } else if (parentViewID == spinnerGua64.getId()) {
            gua64_id = (short) position;
            short position_up = (short) (gua64_id / 8);//上卦
            short position_dn = (short) (gua64_id % 8);//下卦
            spinnerGua8Up.setSelection(position_up);//设置默认值
            spinnerGua8Dn.setSelection(position_dn);
            findGua(position_up, position_dn);
        }

        searchTextView.getPaint().setFakeBoldText(true);
        searchTextView.scrollTo(0, 0);
        searchTextView.postInvalidate();//刷新视图
        if (play_sounds) {//音效
            soundPool_table.play(1, 1, 1, 1, 0, 1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < menu_id.length; i++) {
            menu.add(Menu.NONE, i, i, menu_id[i]);
        }//遍历添加菜单
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                aboutAlertDialog.show();
                break;
            case 1:
                startActivity(HelpActivity);
                break;
            case 2:
                startActivity(SettingsActivity);
                break;
            case 3:
                startActivity(NoteActivity);
                break;
            case 4:
                //Toast.makeText(TableActivity.this, R.string.building, Toast.LENGTH_SHORT).show();
                startActivity(CalendarActivity);
                break;
            default:
                Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                break;


        }
        Toast.makeText(TableActivity.this,
                String.format(getString(R.string.click), menu_id[item.getItemId()]),
                Toast.LENGTH_SHORT).show();
        if (play_sounds) {
            soundPool_table.play(1, 1, 1, 1, 0, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(MusicService);
        System.out.println("table停止工作，音乐服务停止");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stopService(MusicService);
        // System.out.println("table暂停工作，音乐服务停止");
    }

    @Override
    protected void onRestart() {
        super.onRestart();//刷新界面时更新值
        play_music = settings.getBoolean("is_play_music", true);
        play_sounds = settings.getBoolean("is_play_sounds", true);
        first_use = settings.getBoolean("first_use", false);
        can_copy = settings.getBoolean("can_copy", false);

        if (play_music) {//是否启动音乐服务
            startService(MusicService);
        } else {
            stopService(MusicService);
        }

        if (can_copy) {
            searchTextView.setTextIsSelectable(true);//复制文本框文字
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        } else {
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
            searchTextView.setTextIsSelectable(false);//复制文本框文字
        }
        searchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面

        System.out.println("table重新读值成功");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopService(MusicService);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        long currentTime = System.currentTimeMillis();
        //if(exit_time == 0) exit_time = System.currentTimeMillis();
        if ((currentTime - exit_time) < 2500 && press_again) {
            System.exit(0);
            //System.out.println("application 完全退出");
        } else {
            Toast.makeText(TableActivity.this, R.string.back_press, Toast.LENGTH_SHORT).show();
            press_again = true;
            exit_time = currentTime;
        }
    }

    private HashMap<String, String> getGuaMap() {
        try {
            InputStream inStream = getAssets().open("json/gua.json");
            String json = Tools.inputStream2String(inStream);
            JSONObject jsonObject = (JSONObject) new JSONTokener(json).nextValue();
            HashMap<String, String> guaMap = new HashMap<>();
            for (String s : getResources().getStringArray(R.array.gua_64)) {
                guaMap.put(s, jsonObject.getString(s));
            }
            //System.out.println(guaMap.toString());
            return guaMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    //卦象逻辑
    protected void findGua(short up_gua_id, short dn_gua_id) {
        short gua_max = (short) (up_gua_id * 8 + dn_gua_id);
        System.out.println(gua_max);
        String[] guaNames = getResources().getStringArray(R.array.gua_64);
        searchTextView.setText(guaMap.get(guaNames[gua_max]));
    }
}
