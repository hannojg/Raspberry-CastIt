package de.raspberry.castit

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import de.raspberry.castit.api.Calls
import de.raspberry.castit.api.PyCaster
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val api: Calls = PyCaster();
    var cachedVideoType = "video/mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when {
            intent?.action == Intent.ACTION_VIEW -> {
                if(intent.type.contains("video/")) {
                    handleIncomingVideoIntent(intent);
                }
            }

            intent?.action == Intent.ACTION_SEND -> {
                if("text/plain" == intent.type) {
                    handleIncomingUrl(intent);
                }
            }
        }

        registerListener()
    }

    private fun registerListener() {
        btnStop.setOnClickListener { api.stopPlayback() }
        btnVolumneDecrease.setOnClickListener{ api.volumeDown() }
        btnVolumneIncrease.setOnClickListener{ api.volumeUp() }
        btnPlay.setOnClickListener { api.resume() }
        btnPause.setOnClickListener{ api.pause() }
        btnPlayDirect.setOnClickListener { api.playVideoUrl(getMediaUrl()) }
        btnExtract.setOnClickListener { api.extractAndPlay(getMediaUrl()) }
        btnShare.setOnClickListener { handleShare() }
    }

    private fun handleIncomingUrl(intent: Intent?) {
        if(intent != null) {
            cachedVideoType = if(intent.type != null) intent.type else "video/mp4"
            val url = if(intent.clipData != null) intent.clipData.getItemAt(0).text.toString() else intent.dataString
            editTextUrl.setText(url);
        }
    }

    private fun handleIncomingVideoIntent(intent: Intent) {
        editTextUrl.setText(intent.dataString)
    }

    private fun handleShare() {
        val videoUri: Uri = Uri.parse(getMediaUrl())
        val intentShare: Intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(videoUri, cachedVideoType)
        }
        startActivity(Intent.createChooser(intentShare, getString(R.string.shareIntent)))
    }

    private fun getMediaUrl(): String =
        editTextUrl.text.toString()

}
