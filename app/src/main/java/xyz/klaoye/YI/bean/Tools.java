package xyz.klaoye.YI.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tools {
    public static String inputStream2String(InputStream inputStream) throws IOException {
        byte[] b = new byte[inputStream.available()];
        inputStream.read(b);
        inputStream.close();
        return new String(b);
    }

    public static File inputStream2File(InputStream inputStream, String fileName) throws Exception {
        File file = new File(fileName);
        byte[] b = new byte[inputStream.available()];
        if (!file.exists()) file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        inputStream.read(b);
        fileOutputStream.write(b);
        inputStream.close();
        fileOutputStream.close();
        return file;
    }
}
