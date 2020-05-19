package com.example.YI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity {
    AlertDialog inputAlertDialog, isDeleteDialog;//新建文本对话框
    Button addButton;//新建文本悬浮按钮
    View addView;//新建文本视图
    EditText addTittleEdit, addTextEdit;//新建标题，文本
    ArrayAdapter<String> adapter;
    ListView noteListView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        addButton = findViewById(R.id.add_Button);
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

        addButton.setOnClickListener(new View.OnClickListener() {
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
                /*   .setNeutralButton(R.string.out_put, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               funOutBrowse();
                               funOutSave(fileName);
                           }
                   })   */
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog = builder.create();
        return alertDialog;
    }

    //导出笔记文件
    protected void funOutSave(String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            checkPermission();
        }

        String inString;
        String filePathOut = (Environment.DIRECTORY_DOWNLOADS + fileName + ".txt");
        File filePath = new File(getFilesDir() + "/NoteData/" + fileName + ".txt");
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
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

        File file = new File(filePathOut);

        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(inString.getBytes());
            outputStream.close();
            System.out.println("TXT文件：“" + fileName + "” 已存储");
        } catch (IOException e) {
            System.out.println("TXT文件：“" + fileName + "”存储失败");
            e.printStackTrace();
        }
    }

    //将笔记文件写入内部 方法
    protected void funSaveFile(String fileName, String message) {
        String filePathIn = (getFilesDir() + "/NoteData/" + fileName + ".txt");
        // String filePathOut = (Environment.getExternalStorageDirectory()+"/ZhouYINote/"+fileName+".txt");
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
        File file = new File(Environment.DIRECTORY_DOWNLOADS);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (file == null || !file.exists()) {
            return;
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "*/*");

        startActivityForResult(intent, 1);
    }

    //读取内部笔记 方法
    protected String funReadFile(String fileName) {
        String outString;
        File filePath = new File(getFilesDir() + "/NoteData/" + fileName + ".txt");
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
        File file = new File(getFilesDir() + "/NoteData/" + fileName + ".txt");
        file.delete();
    }


    //遍历文件 方法
    protected String[] funNoteList() {
        File filePath = new File(getFilesDir() + "/NoteData/");
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void checkPermission() {
        int targetSdkVersion = 0;
        String[] PermissionString = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("err", "检查权限_err0");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
            //如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.e("err", "所有权限已经授权！");
                    return;
                }
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(this,
                        PermissionString, 1);
            }
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                Log.e("err", "权限" + permission + "没有授权");
                return false;
            }
        }
        return true;
    }

    //申请权限结果返回处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 所有的权限都授予了
                Log.e("err", "权限都授权了");
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                //容易判断错
                //MyDialog("提示", "某些权限未开启,请手动开启", 1) ;
            }
        }
    }
}