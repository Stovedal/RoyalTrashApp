package se.example.trashers.royaltrash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.experimental.*

class ScoreBoardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    data class Score(val name: String, val score: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_score_board)
        viewManager = LinearLayoutManager(this)

        launch {
            val scores =  apiGetHighscores().toCollection(ArrayList())



            launch(UI){
                scores.add(Highscore(1,"FunkyGurkan33", 107, null, null))
                scores.add(Highscore(2,"FunkyGurkan33", 97,null, null))
                scores.add(Highscore(3,"MagganBaggan", 96,null, null))
                scores.add(Highscore(4,"lill-tomten", 83,null, null))
                scores.add(Highscore(5,"VirreBirre", 82,null, null))
                scores.add(Highscore(6,"Luringen", 79,null, null))
                viewAdapter = ScoresAdapter(scores)
                recyclerView = findViewById(R.id.score_scroll)

                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
                viewManager.scrollToPosition(6)
            };
        }
    }

    /**
     * Sends a HTTP POST to server to post a new highscore.
     * Param js needs to be a json string
     */
    fun apiHttpPostToServer(urlString: String, js: String){
        /* Ex URL "http://royaltrashapp.azurewebsites.net/api/highscores/"*/
        /*Ex js "{\"hs_username\":\"Test\",\"hs_score\":1,\"lat\":63.802443,\"lng\":20.320271}"*/
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

    /**
     * Retrieves a json from server and converts to Array with Highscore class
     */
    fun apiGetHighscores():Array<Highscore>{
        val res = URL("http://royaltrashapp.azurewebsites.net/api/highscores").readText(Charsets.UTF_8)
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
        val res = URL("http://royaltrashapp.azurewebsites.net/api/Quizs").readText(Charsets.UTF_8)
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
