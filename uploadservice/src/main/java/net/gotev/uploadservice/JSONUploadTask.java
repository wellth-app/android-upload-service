package net.gotev.uploadservice;

import android.content.Intent;
import android.util.Log;

import net.gotev.uploadservice.http.BodyWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
                    final String paramName = parameter.getName();
                    final String paramValue = parameter.getValue();

                    if (paramName.contains("variables")) {
                        Log.d("JSONUPLOADTASK", "Param name contains variables");
                        // Base64 encode the photo data
                        final JSONObject variablesJSONObject = new JSONObject(paramValue);
                        final JSONObject inputJSONObject = variablesJSONObject.getJSONObject("input");
                        final JSONObject medicationCheckInJSONObject = inputJSONObject.getJSONObject("medicationCheckIn");
                        Log.d("JSONUPLOADTASK", "variablesJSONObject = " + variablesJSONObject.toString());

                        if (medicationCheckInJSONObject.has("photoUrl")) {
                            Log.d("JSONUPLOADTASK", "variablesJSONObject has photoUrl");
                            final String photoURL = medicationCheckInJSONObject.getString("photoUrl");
                            Log.d("JSONUPLOADTASK", "photoURL = "+ photoURL);

                            if (photoURL.contains("file")) {
                                Log.d("JSONUPLOADTASK", "photoURL contains file");
                                medicationCheckInJSONObject.put("photoUrl", EncodingUtils.encodeFileToBase64Binary(new File(photoURL)));
                                inputJSONObject.put("medicationCheckIn", medicationCheckInJSONObject);
                                variablesJSONObject.put("input", inputJSONObject);
                            }
                        }
                        result.put(paramName, variablesJSONObject.toString());
                    } else {
                        result.put(paramName, paramValue);
                    }
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
