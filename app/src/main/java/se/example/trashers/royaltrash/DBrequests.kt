package se.example.trashers.royaltrash

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DBrequests {
    /**
     * Sends a HTTP POST to server to post a new highscore.
     * Param js needs to be a json string
     */
    fun apiHttpPostToServer(urlString: String, js: String){
        /* Ex URL "http://royaltrashapp.azurewebsites.net/api/highscores/"*/
        /*Ex js "{\"hs_username\":\"Test\",\"hs_score\":1,\"lat\":63.802443,\"lng\":20.320271}"*/
        try {
            val obj = URL(urlString).openConnection() as HttpURLConnection
            obj.requestMethod = "POST"
            obj.doOutput=true
            val byte = js.toByteArray()
            val length = byte.size

            obj.setFixedLengthStreamingMode(length)
            obj.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            obj.connect()
            val os = obj.outputStream
            os.write(byte)
            os.flush()
            os.close()

            println("\nSending 'Post' request to URL : ${obj.url}")
            println("Response Code : ${obj.responseCode}")
        }
        catch (e: IOException) {
            println(e.stackTrace)
        }

    }

    /**
     * Retrieves a json from server and converts to Array with Highscore class
     */
    fun apiGetHighscores():Array<Highscore>{
        val res = URL("http://royaltrashapp.azurewebsites.net/api/highscores/getbyscore").readText(Charsets.UTF_8)
        //val highScoreUsers = ob.fromJson(res, Highscore::class.java)
        val gson = Gson()
        val highscoreArray = gson.fromJson(res, Array<Highscore>::class.java)
        /*highscoreArray.sortWith(object: Comparator<Highscore>{
            override fun compare(p1: Highscore, p2: Highscore): Int = when {
                p1.hs_score < p2.hs_score-> 1
                p1.hs_score== p2.hs_score -> 0
                else -> -1
            }
        })*/
        return highscoreArray
    }
    fun apiGetQuiz():Array<Quiz>{
        val res = URL("http://royaltrashapp.azurewebsites.net/api/qq/getqq").readText(Charsets.UTF_8)
        val gson = Gson()
        return gson.fromJson(res, Array<Quiz>::class.java)
    }

    data class Highscore(
            @SerializedName("hs_id") val hs_id: Int,
            @SerializedName("hs_username") val hs_username: String,
            @SerializedName("hs_score") val hs_score: Int,
            @SerializedName("lat") val lat: Float?,
            @SerializedName("description") val lng: Float?
    )

    data class Quiz(
            @SerializedName("id") val hs_id: Int,
            @SerializedName("question") val question: String,
            @SerializedName("C1answer") val answer1: String,
            @SerializedName("C2answer") val answer2: String,
            @SerializedName("C3answer") val answer3: String,
            @SerializedName("C4answer") val answer4: String
    )

}