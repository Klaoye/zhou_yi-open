package com.example.YI;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class NoteActivity extends AppCompatActivity {
    AlertDialog inputAlertDialog;//新建文本对话框
    FloatingActionButton addFloatButton;//新建文本悬浮按钮
    View addview;//新建文本视图
    EditText addTittleEdit, addTextEdit;//新建标题，文本
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        addFloatButton = findViewById(R.id.note_FloatButton_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.wood240));
        }//修改活动栏样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            String[] str_arry = getResources().getStringArray(R.array.table_menu);
            Objects.requireNonNull(getSupportActionBar()).setTitle(str_arry[3]);
        }//设置顶部返回箭头

        //视图注册，控件获取
        addview = getLayoutInflater().inflate(R.layout.write_note_view, null);
        addTittleEdit = addview.findViewById(R.id.note_editText_title);
        addTextEdit = addview.findViewById(R.id.note_editText_text);

        inputAlertDialog = new AlertDialog.Builder(NoteActivity.this)
                .setTitle(R.string.new_text)
                .setView(addview)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })//保存按钮
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addTittleEdit.setText("");
                        addTextEdit.setText("");
                    }
                })//取消按钮
                .create();

        addFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAlertDialog.show();
            }
        });



    }

    protected AlertDialog funAlertDialog(String TittleStr, String message) {
        AlertDialog.Builder ADbuilder = new AlertDialog.Builder(NoteActivity.this);
        AlertDialog alertDialog;
        ADbuilder.setIcon(R.mipmap.ic_launcher)
                .setTitle(TittleStr)
                .setMessage(message)
                .setNegativeButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = ADbuilder.create();
        System.out.println("note AlertDialog" + TittleStr + " 输出");
        return alertDialog;
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
