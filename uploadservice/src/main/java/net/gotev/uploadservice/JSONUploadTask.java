package net.gotev.uploadservice;

import android.content.Intent;

import net.gotev.uploadservice.http.BodyWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Implements an HTTP JSON upload task.
 *
 * @author kyleweisel (Kyle Weisel)
 */
public class JSONUploadTask extends HttpUploadTask {

    @Override
    protected void init(UploadService service, Intent intent) throws IOException {
        super.init(service, intent);
        httpParams.addHeader("Content-Type", "application/json; charset=utf-8");
    }

    @Override
    protected long getBodyLength() throws UnsupportedEncodingException {
        return (getRequestParametersLength());
    }

    @Override
    public void onBodyReady(BodyWriter bodyWriter) throws IOException {
        uploadedBytes = 0;
        writeRequestParameters(bodyWriter);
        broadcastProgress(uploadedBytes, totalBytes);
    }

    private long getRequestParametersLength() throws UnsupportedEncodingException {
        long parametersBytes = 0;
        parametersBytes += this.getRequestParametersJSONObject().toString().getBytes().length;
        return parametersBytes;
    }

    private JSONObject getRequestParametersJSONObject() {
        final JSONObject result = new JSONObject();
        if (!httpParams.getRequestParameters().isEmpty()) {
            for (final NameValue parameter : httpParams.getRequestParameters()) {
                try {
                    result.put(parameter.getName(), parameter.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    private void writeRequestParameters(BodyWriter bodyWriter) throws IOException {
        final JSONObject jsonObject = getRequestParametersJSONObject();
        final byte[] bytes = jsonObject.toString().getBytes();
        bodyWriter.write(bytes);
        uploadedBytes += bytes.length;
        broadcastProgress(uploadedBytes, totalBytes);
    }

    @Override
    protected void onSuccessfulUpload() {
        addAllFilesToSuccessfullyUploadedFiles();
    }

}
