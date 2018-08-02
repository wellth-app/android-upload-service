package net.gotev.uploadservice;

/**
 * Created by Kyle Weisel on 7/22/18.
 */

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class EncodingUtils {
    /**
     * Method used for encode the file to base64 binary format
     * @param filePath
     * @return encoded file format
     */
    public static String encodeFileToBase64Binary(final String filePath){
        Log.d("EncodingUtils", "Running encodeFileToBase64Binary on filePath = " + filePath);

        try {
            final File file = new File(filePath.substring(filePath.lastIndexOf("file:") + 1));
            final byte[] fileBytes = fileToByteArray(file);
            final byte[] base64Bytes = Base64.encodeBase64(fileBytes);
            return new String(base64Bytes);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] fileToByteArray(final File file) throws IOException {
        return readFileToByteArray(file);
    }
}
