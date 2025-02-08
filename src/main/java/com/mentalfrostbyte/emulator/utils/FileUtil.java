package com.mentalfrostbyte.emulator.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    //from: https://sentry.io/answers/inputstream-to-string/
    public static String toString_ByteArrayOutputStream(InputStream stream) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int readBytes = stream.read(buffer);

        while(readBytes != -1){
            outputStream.write(buffer, 0, readBytes);
            readBytes = stream.read(buffer);
        }

        return outputStream.toString();
    }

}
