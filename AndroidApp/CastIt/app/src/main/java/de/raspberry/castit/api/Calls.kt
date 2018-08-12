package de.raspberry.castit.api

interface Calls {
    val HOST: String
        get() = "pycast:8080"

    fun playVideoUrl(url: String)
    fun stopPlayback()
    fun volumeUp()
    fun volumeDown()
    fun pause()
    fun resume()
    fun extractAndPlay(url: String)
}