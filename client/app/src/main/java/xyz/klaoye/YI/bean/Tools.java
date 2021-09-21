package xyz.klaoye.YI.bean;

import java.io.InputStream;

public class Tools {
    public static String inputStream2String(InputStream inStream) throws Exception {
        byte[] b = new byte[inStream.available()];
        inStream.read(b);
        return new String(b);
    }
}
