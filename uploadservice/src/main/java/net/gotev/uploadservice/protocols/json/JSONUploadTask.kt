package net.gotev.uploadservice.protocols.json

import net.gotev.uploadservice.HttpUploadTask
import net.gotev.uploadservice.extensions.addHeader
import net.gotev.uploadservice.network.BodyWriter

import android.util.Base64
import org.json.JSONObject
import java.io.File

/**
 * Implements an HTTP Multipart upload task.
 */
class JSONUploadTask : HttpUploadTask() {

    /*
     * Encodes a file at a file path to Base64
     */
    fun encodeFileToBase64(filePath: String): String {
        try {
            val trimmedPath = filePath.substring(filePath.indexOf("file://") + 7)
            val file = File(trimmedPath)
            val fileBytes = file.readBytes()
            return Base64.encodeToString(fileBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return ""
        }
    }

    /*
     * Converts the request parameters to a JSONObject
     */
    fun getRequestParametersAsJSONObject(): JSONObject {
        val regex = """((file):((//)|(\\))+[\w\d:#@%/;$()~_?\+-=\\\.&]*)""".toRegex()
        val result = JSONObject()

        // Build a JSONObject from the parameter ArrayList
        httpParams.requestParameters.forEach {
            val paramName = it.name
            var paramValue = it.value
            val matches = regex.findAll(paramValue)

            matches.forEach {
                paramValue = paramValue.replace(it.value, encodeFileToBase64(it.value))
            }

            // If the parameter value starts with a {, it is a JSONObject,
            // else it'll be a string
            if (paramValue.startsWith("{")) {
                result.put(paramName, JSONObject(paramValue))
            } else {
                result.put(paramName, paramValue)
            }
        }

        return result
    }

    private fun BodyWriter.writeRequestParameters() {
        var requestParameters = getRequestParametersAsJSONObject()
        var requestParametersBytes = requestParameters.toString().toByteArray()
        write(requestParametersBytes)
    }

    private val requestParametersLength: Long
        get() = getRequestParametersAsJSONObject().toString().toByteArray().size.toLong()

    override val bodyLength: Long
        get() = requestParametersLength

    override fun performInitialization() {
        httpParams.requestHeaders.apply {
            addHeader("Content-Type", "application/json; charset=utf-8")
        }
    }

    override fun onWriteRequestBody(bodyWriter: BodyWriter) {
        resetUploadedBytes()
        setAllFilesHaveBeenSuccessfullyUploaded(false)

        bodyWriter.apply {
            writeRequestParameters()
        }
    }
}
