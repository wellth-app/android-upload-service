package net.gotev.uploadservice;

/**
 * Created by Kyle Weisel on 7/22/18.
 */

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EncodingUtils {
    /**
     * Method used for encode the file to base64 binary format
     * @param file
     * @return encoded file format
     */
    public static String encodeFileToBase64Binary(File file){
        Log.d("EncodingUtils", "Running encodeFileToBase64Binary on file = " + file.getAbsolutePath());

        String encodedFile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            final byte[] byteArr = Base64.encodeBase64(bytes);
            encodedFile = new String(byteArr);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedFile;
    }
}
