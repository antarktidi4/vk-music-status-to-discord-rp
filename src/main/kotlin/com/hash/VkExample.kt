package com.hash

import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class VkExample(private val vkToken: String, private val vkTargetID: String, private val clientID: String) {
    private val templates = PresenceTemplates()
    private val richPresence: DiscordRichPresence
    private var work = true

    init {
        richPresence = DiscordRichPresence(clientID)
        richPresence.setPresence(templates.getIDLETemplate())
    }

    fun start() {
        richPresence.run()
        Thread {
            var tmpTitle = ""
            var now = System.currentTimeMillis() / 1000L
            while (work) {
                val status = getAudioFromStatus()
                if (status == "IDLE") {
                    richPresence.setPresence(templates.getIDLETemplate())
                } else {
                    val audio = JSONObject(status)
                    val title = audio.getString("title")
                    val artist = audio.getString("artist")
                    if(tmpTitle != title) {
                        now = System.currentTimeMillis() / 1000L
                        tmpTitle = title
                    }
                    val template = templates.getMusicTemplate(title, artist, now)
                    richPresence.setPresence(template)
                }
                Thread.sleep(2000)
            }
        }.start()
    }

    fun stop() {
        work = false
        richPresence.stop()
    }

    fun getAudioFromStatus(): String {
        val response = JSONObject(sendGetRequest( "https://api.vk.com/method/status.get?", mapOf("user_ids" to vkTargetID, "access_token" to vkToken, "v" to "5.31"))).getJSONObject("response")
        return if (response.has("audio")) response.getJSONObject("audio").toString() else "IDLE"
    }

    fun sendGetRequest(url: String, params: Map<String, String> = mapOf()): String {
        val urlParams = params.map { (k, v) -> "$k=$v" }.joinToString("&")
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .headers("Content-Type", "text/plain;charset=UTF-8")
            .uri(URI.create("$url$urlParams"))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}

class PresenceTemplates {

    fun getIDLETemplate(): String {
        return """{
            "state": "IDLE",
            "assets": {
                "large_text": "ультрахуёв",
                "large_image": "note"
            }}""".trimMargin()
    }

    fun getMusicTemplate(songName: String, songAuthor: String, startTime: Long): String {
        return """{
            "state": "$songAuthor",
            "details": "$songName",
            "timestamps": {
                "start": $startTime
            }, "assets": {
                "large_text": "ультрахуёв",
                "large_image": "note"
            }}""".trimMargin()
    }

}