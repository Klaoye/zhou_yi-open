package com.example.YI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableActivity extends AppCompatActivity {
    Button BtnSearch;//检索键
    Spinner spinner_gua_min_up;//下拉框上卦
    Spinner spinner_gua_min_dn;//下拉框下卦
    Spinner spinner_gua_max;//下拉框六十四卦
    SimpleAdapter adapter_gua_min;//八卦适配器
    SimpleAdapter adapter_gua_max;//六十四卦适配器
    SharedPreferences settings;//共享存储库
    SharedPreferences.Editor editor;//共享存储库编辑器

    SoundPool soundPool_table;//音频池

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
    private TextView SearchTextView;//文字界面
    private Typeface typefaceKAI;//字体
    private Switch modelSwitch;//模式开关
    private int up_gua_min_id;//上卦
    private int dn_gua_min_id;//下卦
    private int gua_max_id;//六十四卦
    private boolean model;//模式
    private long exitTime = 0;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        Global global = new Global();

        //实例化Intent
        SettingsActivity = new Intent(TableActivity.this, SettingsActivity.class);
        MusicService = new Intent(TableActivity.this, MusicService.class);
        NoteActivity = new Intent(TableActivity.this, NoteActivity.class);
        HelpActivity = new Intent(TableActivity.this, HelpActivity.class);
        CalendarActivity = new Intent(TableActivity.this, CalendarActivity.class);

        menu_id = getResources().getStringArray(R.array.table_menu);//菜单列表数组

        soundPool_table = global.MySoundPool(TableActivity.this);

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
        typefaceKAI = Typeface.createFromAsset(getAssets(), "fonts/kai.ttf");//注册字体

        //********列表带图片***********
        spinner_gua_min_up = findViewById(R.id.spinner_gua_min_up);//上卦
        spinner_gua_min_dn = findViewById(R.id.spinner_gua_min_dn);//下挂
        spinner_gua_max = findViewById(R.id.spinner_gua_max);//六十四卦

        TypedArray imageID = getResources().obtainTypedArray(R.array.table_menu_drawable);
        //图片ID数组

        String[] gua_min = getResources().getStringArray(R.array.gua_min);//列表文字数组-八卦
        String[] gua_max = getResources().getStringArray(R.array.gua_max);//六十四卦

        List<Map<String, Object>> List_gua_min = new ArrayList<Map<String, Object>>();//创建八卦文字列表
        //遍历图片及文字
        for (int i = 0; i < imageID.length(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imageID.getResourceId(i, 0));
            map.put("text", gua_min[i]);
            List_gua_min.add(map);
        }

        List<Map<String, Object>> List_gua_max = new ArrayList<Map<String, Object>>();//创建六十四卦文字列表
        //遍历文字
        for (String guaMax : gua_max) {
            Map<String, Object> map = new HashMap<String, Object>();
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

        //列表选值事件 上卦
        spinner_gua_min_up.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int id_up, long l) {
                up_gua_min_id = (int) adapter_gua_min.getItemId(id_up);
                if (play_sounds) {
                    soundPool_table.play(1, 1, 1, 1, 0, 1);
                }//

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //列表选值事件 下卦
        spinner_gua_min_dn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int id_dn, long l) {
                dn_gua_min_id = (int) adapter_gua_min.getItemId(id_dn);
                if (play_sounds) {
                    soundPool_table.play(1, 1, 1, 1, 0, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //列表选值事件 六十四卦
        spinner_gua_max.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int max_id, long l) {
                gua_max_id = (int) adapter_gua_max.getItemId(max_id);
                if (play_sounds) {
                    soundPool_table.play(1, 1, 1, 1, 0, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SearchTextView = findViewById(R.id.textView_search);//文本框
        SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面


        BtnSearch = findViewById(R.id.btn_search);
        BtnSearch.setText(R.string.search);
        BtnSearch.setTextSize(33);//字号
        BtnSearch.setTypeface(typefaceKAI);//字体
        BtnSearch.getPaint().setFakeBoldText(true);//绘制字体
        modelSwitch.setTextSize(15);

        modelSwitch.setTextSize(15);

        //模式监听
        modelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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

            }
        });

        //按钮单击事件
        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //逻辑匹配
                if (!model) {//检索模式
                    //通过以下操作实现“选八卦得出六十四卦”
                    int position_max = up_gua_min_id * 8 + dn_gua_min_id;//六十四卦
                    spinner_gua_max.setSelection(position_max);//设置默认值
                    FindGua(up_gua_min_id, dn_gua_min_id);
                } else {//搜索模式
                    //通过以下操作实现“选六十四卦得出八卦”
                    int position_up = gua_max_id / 8;//上卦
                    int position_dn = gua_max_id % 8;//下卦
                    spinner_gua_min_up.setSelection(position_up);//设置默认值
                    spinner_gua_min_dn.setSelection(position_dn);
                    FindGua(position_up, position_dn);
                }
                //修改字体
                SearchTextView.setTypeface(typefaceKAI);
                SearchTextView.getPaint().setFakeBoldText(true);

                SearchTextView.scrollTo(0, 0);
                SearchTextView.postInvalidate();//刷新视图

                if (play_sounds) {
                    soundPool_table.play(2, 1, 1, 1, 0, 1);
                }
            }
        });
        if (canCopy) {
            SearchTextView.setTextIsSelectable(true);//复制文本框文字
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        } else {
            SearchTextView.setTextIsSelectable(false);
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        }

        //开发者信息
        about_alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.company)
                .setIcon(R.drawable.company)
                .setMessage(getString(R.string.developer_information) + getString(R.string.suggestion) + getString(R.string.donate_information))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.donate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        //第一次使用引导
        first_use_alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.first_use_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.first_wse_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(HelpActivity);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if (first_use) {
            editor.putBoolean("first_use", false).apply();
            first_use = settings.getBoolean("first_use", first_use);
            first_use_alertDialog.show();
        }

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
            SearchTextView.setTextIsSelectable(true);//复制文本框文字
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
        } else {
            // SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面
            SearchTextView.setTextIsSelectable(false);//复制文本框文字
        }
        SearchTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动字面

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

    //卦象逻辑
    protected void FindGua(int up_gua_min_id, int dn_gua_min_id) {
        switch (up_gua_min_id) {
            case 0:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.qian1);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.lv);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.tong_ren);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.wu_wang);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.gou);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.song);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.dun);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.fou);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 1:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.guai);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.dui);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.ge);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.sui);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.da_guo);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.kun47);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.xian);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.cui);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 2:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.da_you);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.kui);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.li);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.shi_ke);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.ding);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.wei_ji);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.lv56);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.jin);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 3:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.da_zhuang);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.gui_mei);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.feng);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.zhen);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.heng);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.jie40);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.xiao_guo);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.yu);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 4:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.xiao_xu);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.zhong_fu);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.jia_ren);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.yi42);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.xun);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.huan);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.jian53);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.guan);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 5:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.xv);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.jie60);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.ji_ji);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.zhun);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.jing);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.kan);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.jian39);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.bi);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 6:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.da_xu);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.sun);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.ben);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.yi27);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.gu);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.meng);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.gen);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.bo);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;
            case 7:
                switch (dn_gua_min_id) {
                    case 0:
                        SearchTextView.setText(R.string.tai);
                        break;
                    case 1:
                        SearchTextView.setText(R.string.lin);
                        break;
                    case 2:
                        SearchTextView.setText(R.string.ming_yi);
                        break;
                    case 3:
                        SearchTextView.setText(R.string.fu);
                        break;
                    case 4:
                        SearchTextView.setText(R.string.shen);
                        break;
                    case 5:
                        SearchTextView.setText(R.string.shi);
                        break;
                    case 6:
                        SearchTextView.setText(R.string.qian15);
                        break;
                    case 7:
                        SearchTextView.setText(R.string.kun2);
                        break;
                    default:
                        Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        SearchTextView.setText("");
                        break;
                }
                break;


            default:
                Toast.makeText(TableActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                SearchTextView.setText("");
                break;

        }
    }
}
