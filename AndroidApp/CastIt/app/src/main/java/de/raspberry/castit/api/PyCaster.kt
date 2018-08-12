package de.raspberry.castit.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync

class PyCaster: Calls {
    val httpClient: OkHttpClient = OkHttpClient();

    override fun extractAndPlay(url: String) {
        executeRequest("extracturl", url)
    }

    override fun pause() {
        executeRequest("performaction", "pause")
    }

    override fun resume() {
        executeRequest("performaction", "pause")
    }

    override fun playVideoUrl(url: String) {
        executeRequest("url", url)
    }


    override fun stopPlayback() {
        executeRequest("performaction", "kill")
    }

    override fun volumeUp() {
        executeRequest("performaction", "volumeup")
    }

    override fun volumeDown() {
        executeRequest("performaction", "volumedown")
    }

    fun executeRequest(key: String, value: String) {
        executeRequest(Pair(key, value))
    }

    fun executeRequest(data: Pair<String, String>) {
        doAsync {
            val requestUrl = buildRequestUrl(data);
            Log.d("PyCaster", "API requestUrl: $requestUrl")
            val response = httpClient.newCall(Request.Builder()
                    .url(requestUrl)
                    .build())
                    .execute();

            if (response.isSuccessful) {
                Log.d("PyCaster", "Called PyCaster")
            } else {
                Log.e("PyCaster", "Call playVideoUrl ended in unsuccessful response")
            }
        }
    }

    fun buildRequestUrl(data: Pair<String, String>): String {
        return "http://${HOST}/?${data.first}=${data.second}";
    }

}