package com.mywings.appschedulling.process

import android.os.AsyncTask
import org.json.JSONObject

class RegisterDeviceAsync : AsyncTask<JSONObject?, Void, String?>() {

    private lateinit var onRegisterDeviceListener: OnRegisterDeviceListener

    override fun doInBackground(vararg param: JSONObject?): String? {
        return HttpConnectionUtil().requestPost(ConstantUrl.URL + ConstantUrl.REGISTER_DEVICE, param[0])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onRegisterDeviceListener.onDeviceRegisteredSuccess(result!!)
    }

    fun setOnRegisterDeviceListener(onRegisterDeviceListener: OnRegisterDeviceListener, request: JSONObject?) {
        this.onRegisterDeviceListener = onRegisterDeviceListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }

}