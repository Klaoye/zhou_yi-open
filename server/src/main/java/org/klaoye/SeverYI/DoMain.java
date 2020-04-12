package org.klaoye.SeverYI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DoMain {
    public static void main(String[] args) {
        System.out.println(JSONarray());
        SaveJSON(JSONarray());
    }

    protected static JSONArray JSONarray(){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();



        jsonArray.add(jsonObject);
        jsonArray.add(jsonObject2);
        return jsonArray;
    }

    protected static void SaveJSON(JSONArray jsonArray){
        String string = "./new.json";
        File file = new File(string);
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(jsonArray.toJSONString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
