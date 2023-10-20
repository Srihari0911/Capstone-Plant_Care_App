package com.funetuneapps.bloombundy.notifications

import android.app.Activity
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.funetuneapps.bloombundy.R
import org.json.JSONException
import org.json.JSONObject


object SenderNotification {

    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    var js_key = ""

    fun sendNotifications(
        userFcmToken: String,
        title: String,
        body: String,
        mActivity: Activity
    ) {
        Log.i("skjhfjsh", " $body")
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            notiObject.put("icon", R.drawable.app_icon) // enter icon that exists in drawable only
            mainObj.put("notification", notiObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, postUrl, mainObj,
                Response.Listener {
                    // code run is got response
                },
                Response.ErrorListener {
                    // code run is got error
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$js_key"
                    return header
                }
            }
            requestQueue?.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
