package net.gotev.uploadservice.protocols.json

import android.content.Context
import net.gotev.uploadservice.HttpUploadRequest
import net.gotev.uploadservice.UploadTask

class JSONUploadRequest(context: Context, serverUrl: String) :
    HttpUploadRequest<JSONUploadRequest>(context, serverUrl) {

    override val taskClass: Class<out UploadTask>
        get() = JSONUploadTask::class.java
}