package com.example.YI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity {
    AlertDialog inputAlertDialog, isDeleteDialog;//新建文本对话框
    FloatingActionButton addFloatButton;//新建文本悬浮按钮
    View addView;//新建文本视图
    EditText addTittleEdit, addTextEdit;//新建标题，文本
    ArrayAdapter<String> adapter;
    ListView noteListView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        addFloatButton = findViewById(R.id.note_FloatButton_add);
        noteListView = findViewById(R.id.list_note);
        intent = new Intent(NoteActivity.this, NoteActivity.class);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, funNoteList());


        noteListView.setAdapter(adapter);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.wood240));
        }//修改活动栏样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            String[] str_arry = getResources().getStringArray(R.array.table_menu);
            Objects.requireNonNull(getSupportActionBar()).setTitle(str_arry[3]);
        }//设置顶部返回箭头

        //视图注册，控件获取
        addView = getLayoutInflater().inflate(R.layout.write_note_view, null);
        addTittleEdit = addView.findViewById(R.id.note_editText_title);
        addTextEdit = addView.findViewById(R.id.note_editText_text);

        inputAlertDialog = new AlertDialog.Builder(NoteActivity.this)
                .setTitle(R.string.new_text)
                .setView(addView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (addTittleEdit.getText().toString() != null) {
                            funSaveFile(addTittleEdit.getText().toString(), addTittleEdit.getText().toString());
                            addTittleEdit.setText(null);
                            addTextEdit.setText(null);
                            noteListView.invalidate();
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(NoteActivity.this, R.string.cant_null, Toast.LENGTH_LONG).show();
                        }
                    }
                })//保存按钮
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addTittleEdit.setText(null);
                        addTextEdit.setText(null);
                    }
                })//取消按钮
                .create();

        addFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAlertDialog.show();
            }
        });

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                funAlertDialog(funNoteList()[position], funReadFile(funNoteList()[position])).show();
            }
        });
        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                funOperationDialog(funNoteList()[position]).show();
                return true;
            }
        });
    }

    //显示 笔记 弹窗方法
    protected AlertDialog funAlertDialog(final String fileName, String message) {
        AlertDialog.Builder ADbuilder = new AlertDialog.Builder(NoteActivity.this);
        AlertDialog alertDialog;
        View view = getLayoutInflater().inflate(R.layout.write_note_view, null);
        final EditText tittle_editor = view.findViewById(R.id.note_editText_title);
        final EditText text_editor = view.findViewById(R.id.note_editText_text);
        tittle_editor.setText(fileName);
        text_editor.setText(funReadFile(fileName));
        tittle_editor.setFocusable(false);
        tittle_editor.setKeyListener(null);
        ADbuilder.setIcon(R.mipmap.ic_launcher)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tittle_editor.getText().toString() != null) {
                            funSaveFile(tittle_editor.getText().toString(), text_editor.getText().toString());
                            System.out.println("note AlertDialog" + fileName + " 已输出");
                        } else {
                            Toast.makeText(NoteActivity.this, R.string.cant_null, Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        funDeleteFile(fileName);
                        noteListView.invalidate();
                        finish();
                        startActivity(intent);
                        System.out.println("note AlertDialog" + fileName + " 已删除");
                    }
                });
        alertDialog = ADbuilder.create();
        return alertDialog;
    }

    //列表长按弹窗 方法
    protected AlertDialog funOperationDialog(final String fileName) {
        AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        builder.setTitle(R.string.note_operation)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        funDeleteFile(fileName);
                        finish();
                        startActivity(intent);
                    }
                })
                /*    .setNeutralButton(R.string.out_put, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             funOutSave(fileName);
                             funOutBrowse();
                        }
                    }) */
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog = builder.create();
        return alertDialog;
    }

    //导出笔记文件
    protected void funOutSave(String fileName) {
        String inString;
        File filePathIn = new File("/data/data/com.example.YI/NoteData/" + fileName + ".txt");
        File filePathOut = new File("/storage/emulated/0/ZhouYINote/" + fileName + ".txt");
        try {
            FileInputStream inputStream = new FileInputStream(filePathIn);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String string;
            while ((string = reader.readLine()) != null) {
                builder.append(string);
            }
            reader.close();
            inString = builder.toString();
        } catch (IOException eIOE) {
            eIOE.printStackTrace();
            inString = "error";
        }

        try {
            filePathOut.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(filePathOut);
            outputStream.write(inString.getBytes());
            outputStream.close();
            System.out.println("TXT文件：“" + fileName + "” 已转储");
        } catch (IOException e) {
            System.out.println("TXT文件：“" + fileName + "”转储失败");
            e.printStackTrace();
        }

    }

    //将笔记文件写入内部 方法
    protected void funSaveFile(String fileName, String message) {
        String filePathIn = ("data/data/com.example.YI/NoteData/" + fileName + ".txt");
        //  String filePathOut = ("/storage/emulated/0/ZhouYINote/"+fileName+".txt");
        File file = new File(filePathIn);

        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(message.getBytes());
            outputStream.close();
            System.out.println("TXT文件：“" + fileName + "” 已存储");
        } catch (IOException e) {
            System.out.println("TXT文件：“" + fileName + "”存储失败");
            e.printStackTrace();
        }
    }

    //输出文件浏览
    protected void funOutBrowse() {
        File file = new File("/storage/emulated/0/ZhouYINote/");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (file == null || !file.exists()) {
            return;
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "*/*");

        startActivityForResult(Intent.createChooser(intent, getString(R.string.file_path)), 1);
    }

    //读取内部笔记 方法
    protected String funReadFile(String fileName) {
        String outString;
        File filePath = new File("/data/data/com.example.YI/NoteData/" + fileName + ".txt");
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String string;
            while ((string = reader.readLine()) != null) {
                builder.append(string);
            }
            reader.close();
            outString = builder.toString();
        } catch (IOException eIOE) {
            eIOE.printStackTrace();
            outString = "error";
        }
        return outString;
    }

    //删除 方法
    protected void funDeleteFile(String fileName) {
        File file = new File("/data/data/com.example.YI/NoteData/" + fileName + ".txt");
        file.delete();
    }


    //遍历文件 方法
    protected String[] funNoteList() {
        File filePath = new File("/data/data/com.example.YI/NoteData/");
        String fileName;
        File[] files = filePath.listFiles();
        assert files != null;
        String[] strings = new String[files.length];
        int dot = 0;
        for (int length = 0; length < files.length; length++) {
            fileName = files[length].getName();
            if (fileName.length() > 0) {
                dot = fileName.lastIndexOf('.');
            }
            strings[length] = fileName.substring(0, dot);
            System.out.println("列" + strings[length] + "遍历完成");
        }

        System.out.println("笔记列表 遍历完成");

        return strings;
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            System.out.println("note已销毁");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                    String sdCard = Environment.getExternalStorageState();
                    if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(this, "获得授权", Toast.LENGTH_LONG).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NoteActivity.this, "buxing", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
