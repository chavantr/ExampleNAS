package com.mywings.appschedulling.process

import android.os.AsyncTask
import org.json.JSONObject

class UploadMetadataAsync : AsyncTask<JSONObject, Void, String?>() {

    private lateinit var onAppMetadataListener: OnAppMetadataListener

    override fun doInBackground(vararg param: JSONObject?): String? {
        return HttpConnectionUtil().requestPost(ConstantUrl.URL + ConstantUrl.UPLOAD_METADATA, param[0])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onAppMetadataListener.onAppMetadataSuccess(result)
    }

    fun setOnMetadataListener(onAppMetadataListener: OnAppMetadataListener, request: JSONObject) {
        this.onAppMetadataListener = onAppMetadataListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }

}