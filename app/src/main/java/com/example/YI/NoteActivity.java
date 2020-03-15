package com.example.YI;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        addFloatButton = findViewById(R.id.note_FloatButton_add);
        noteListView = findViewById(R.id.list_note);

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
                            NoteActivity.this.onRestart();
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
                isDeleteDialog = new AlertDialog.Builder(NoteActivity.this)
                        .setTitle(R.string.is_delete)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                funDeleteFile(funNoteList()[position]);
                                noteListView.invalidate();
                                NoteActivity.this.onRestart();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                return true;
            }
        });
    }

    //显示 笔记 弹窗方法
    protected AlertDialog funAlertDialog(final String TittleStr, String message) {
        AlertDialog.Builder ADbuilder = new AlertDialog.Builder(NoteActivity.this);
        AlertDialog alertDialog;
        View view = getLayoutInflater().inflate(R.layout.write_note_view, null);
        final EditText tittle_editor = view.findViewById(R.id.note_editText_title);
        final EditText text_editor = view.findViewById(R.id.note_editText_text);
        tittle_editor.setText(TittleStr);
        text_editor.setText(funReadFile(TittleStr));
        tittle_editor.setFocusable(false);
        tittle_editor.setKeyListener(null);
        ADbuilder.setIcon(R.mipmap.ic_launcher)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tittle_editor.getText().toString() != null) {
                            funSaveFile(tittle_editor.getText().toString(), text_editor.getText().toString());
                        } else {
                            Toast.makeText(NoteActivity.this, R.string.cant_null, Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        funDeleteFile(TittleStr);
                        noteListView.invalidate();
                        NoteActivity.this.onRestart();
                    }
                });
        alertDialog = ADbuilder.create();
        System.out.println("note AlertDialog" + TittleStr + " 已输出");
        return alertDialog;
    }

    //将笔记文件写入内部 方法
    protected void funSaveFile(String fileName, String message) {
        String filePath = ("data/data/com.example.YI/NoteData/" + fileName + ".txt");
        File file = new File(filePath);

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

    //读取内部笔记 方法
    protected String funReadFile(String fileName) {
        String outString = null;
        File filePath = new File("/data/data/com.example.YI/NoteData/" + fileName + ".txt");
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String string = null;
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
}
