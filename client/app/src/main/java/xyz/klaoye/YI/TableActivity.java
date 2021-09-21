package xyz.klaoye.YI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
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

import xyz.klaoye.YI.base.Global;
import xyz.klaoye.YI.base.Tools;

public class TableActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button BtnSearch;//检索键
    Spinner spinner_gua_min_up;//下拉框上卦
    Spinner spinner_gua_min_dn;//下拉框下卦
    Spinner spinner_gua_max;//下拉框六十四卦
    SimpleAdapter adapter_gua_min;//八卦适配器
    SimpleAdapter adapter_gua_max;//六十四卦适配器
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
    boolean canCopy;
    AlertDialog about_alertDialog;
    AlertDialog first_use_alertDialog;
    TextView searchTextView;//文字界面
    Typeface typefaceKAI;//字体
    Switch modelSwitch;//模式开关
    int up_gua_min_id;//上卦
    int dn_gua_min_id;//下卦
    int gua_max_id;//六十四卦
    boolean model;//模式
    long exitTime = 0;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

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
        canCopy = settings.getBoolean("can_copy", false);

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

        modelSwitch = findViewById(R.id.switch_model);
        model = false;//模式初始值
        typefaceKAI = Typeface.createFromAsset(getAssets(), "fonts/Song.otf");//注册字体

        //********列表带图片***********
        spinner_gua_min_up = findViewById(R.id.spinner_gua_min_up);//上卦
        spinner_gua_min_dn = findViewById(R.id.spinner_gua_min_dn);//下挂
        spinner_gua_max = findViewById(R.id.spinner_gua_max);//六十四卦

        TypedArray imageID = getResources().obtainTypedArray(R.array.table_menu_drawable);
        //图片ID数组

        String[] gua_min = getResources().getStringArray(R.array.gua_min);//列表文字数组-八卦
        String[] gua_max = getResources().getStringArray(R.array.gua_max);//六十四卦

        ArrayList<Map<String, Object>> List_gua_min = new ArrayList<Map<String, Object>>();//创建八卦文字列表
        //遍历图片及文字
        for (int i = 0; i < imageID.length(); i++) {
            Map<String, Object> map = new HashMap();
            map.put("image", imageID.getResourceId(i, 0));
            map.put("text", gua_min[i]);
            List_gua_min.add(map);
        }

        ArrayList<Map<String, String>> List_gua_max = new ArrayList();//创建六十四卦文字列表
        /* 遍历文字 */
        for (String guaMax : gua_max) {
            Map<String, String> map = new HashMap();
            map.put("text", guaMax);
            List_gua_max.add(map);
        }

        //创建八卦适配器
        adapter_gua_min = new SimpleAdapter(this, List_gua_min, R.layout.gua_item_min,
                new String[]{"text", "image"}, new int[]{R.id.textView_gua_min, R.id.imageView_gua});
        //创建六十四卦适配器
        adapter_gua_max = new SimpleAdapter(this, List_gua_max, R.layout.gua_item_max,
                new String[]{"text"}, new int[]{R.id.textView_gua_max});
        //上卦装载适配器
        spinner_gua_min_up.setAdapter(adapter_gua_min);
        //下挂装载适配器
        spinner_gua_min_dn.setAdapter(adapter_gua_min);
        //六十四卦装载适配器
        spinner_gua_max.setAdapter(adapter_gua_max);
        //*******************************

        searchTextView = findViewById(R.id.textView_search);//文本框
        searchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面


        BtnSearch = findViewById(R.id.btn_search);
        BtnSearch.setText(R.string.search);
        BtnSearch.setTextSize(33);//字号
        BtnSearch.setTypeface(typefaceKAI);//字体
        BtnSearch.getPaint().setFakeBoldText(true);//绘制字体
        modelSwitch.setTextSize(15);

        modelSwitch.setTextSize(15);

        //模式监听
        modelSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    modelSwitch.setThumbResource(R.drawable.switch_thumb_white);
                }
                if (play_sounds) {
                    soundPool_table.play(3, 1, 1, 1, 0, 1);
                }
                model = true;
                modelSwitch.setText(R.string.gua_mean);
                Toast.makeText(TableActivity.this, R.string.please_click, Toast.LENGTH_SHORT).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    modelSwitch.setThumbResource(R.drawable.switch_thumb_black);
                }
                if (play_sounds) {
                    soundPool_table.play(4, 1, 1, 1, 0, 1);
                }
                model = false;
                modelSwitch.setText(R.string.gua_drawable);
            }

        });

        BtnSearch.setOnClickListener(this);
        spinner_gua_min_up.setOnItemSelectedListener(this);
        spinner_gua_min_dn.setOnItemSelectedListener(this);
        spinner_gua_max.setOnItemSelectedListener(this);
        if (canCopy) {
            searchTextView.setTextIsSelectable(true);//复制文本框文字
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        } else {
            searchTextView.setTextIsSelectable(false);
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        }

        //开发者信息
        about_alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.company)
                .setIcon(R.drawable.company)
                .setMessage(getString(R.string.developer_information) + getString(R.string.suggestion) + getString(R.string.donate_information))
                .setPositiveButton(R.string.ok, (dialog, which) -> { })
                .setNegativeButton(R.string.donate, (dialog, which) -> { })
                .create();

        //第一次使用引导
        first_use_alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.first_use_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.first_wse_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> startActivity(HelpActivity))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .create();

        if (first_use) {
            editor.putBoolean("first_use", false).apply();
            first_use = settings.getBoolean("first_use", first_use);
            first_use_alertDialog.show();
        }
        guaMap = getGuaMap();

    }

    //按钮单击事件
    @Override
    public void onClick(View v) {
        //逻辑匹配
        if (!model) {//检索模式
            //通过以下操作实现“选八卦得出六十四卦”
            int position_max = up_gua_min_id * 8 + dn_gua_min_id;//六十四卦
            spinner_gua_max.setSelection(position_max);//跳转卦名
            findGua(up_gua_min_id, dn_gua_min_id);
        } else {//搜索模式
            //通过以下操作实现“选六十四卦得出八卦”
            int position_up = (short) (gua_max_id / 8);//上卦
            int position_dn = (short) (gua_max_id % 8);//下卦
            spinner_gua_min_up.setSelection(position_up);//设置默认值
            spinner_gua_min_dn.setSelection(position_dn);
            findGua(position_up, position_dn);
        }
        //修改字体
        searchTextView.setTypeface(typefaceKAI);
        searchTextView.getPaint().setFakeBoldText(true);

        searchTextView.scrollTo(0, 0);
        searchTextView.postInvalidate();//刷新视图

        if (play_sounds) {
            soundPool_table.play(2, 1, 1, 1, 0, 1);
        }
    }

    //点击下拉框
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Adapter adapter = parent.getAdapter();
        if(parent.equals(spinner_gua_min_up)){//上卦
            up_gua_min_id = (short) adapter.getItemId(position);
        }else if(parent.equals(spinner_gua_min_dn)){//下卦
            dn_gua_min_id = (short) adapter.getItemId(position);
        }else if(parent.equals(spinner_gua_max)){//六十四卦
            gua_max_id = (short) adapter.getItemId(position);
        }
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
                about_alertDialog.show();
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
                Toast.makeText(TableActivity.this, R.string.building, Toast.LENGTH_SHORT).show();
                //startActivity(CalendarActivity);
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
        canCopy = settings.getBoolean("can_copy", false);

        if (play_music) {//是否启动音乐服务
            startService(MusicService);
        } else {
            stopService(MusicService);
        }

        if (canCopy) {
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
        if ((currentTime - exitTime) >= 2500) {
            Toast.makeText(TableActivity.this, R.string.BackPressend, Toast.LENGTH_SHORT).show();
            exitTime = currentTime;
        } else {
            System.exit(0);
            System.out.println("application 完全退出");
        }
    }

    private HashMap<String, String> getGuaMap() {
        try {
            InputStream inStream = getAssets().open("json/gua.json");
            String json = Tools.inputStream2String(inStream);
            JSONObject jsonObject = (JSONObject) new JSONTokener(json).nextValue();
            HashMap<String, String> guaMap = new HashMap<>();
            for (String s : getResources().getStringArray(R.array.gua_max)) {
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
    protected void findGua(int up_gua_id, int dn_gua_id) {
        int gua_max = up_gua_id * 8 + dn_gua_id;
        System.out.println(gua_max);
        String[] guaNames = getResources().getStringArray(R.array.gua_max);
        searchTextView.setText(guaMap.get(guaNames[gua_max]));
    }
}
