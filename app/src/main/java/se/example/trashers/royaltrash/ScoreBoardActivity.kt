package se.example.trashers.royaltrash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.net.HttpURLConnection
import java.net.URL


class ScoreBoardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    data class Score(val name: String, val score: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_score_board)
        val scores = ArrayList<Score>()
        scores.add(Score("FunkyGurkan33", 107))
        scores.add(Score("FunkyGurkan33", 97))
        scores.add(Score("MagganBaggan", 96))
        scores.add(Score("lill-tomten", 83))
        scores.add(Score("VirreBirre", 82))
        scores.add(Score("Luringen", 79))

        viewManager = LinearLayoutManager(this)
        viewAdapter = ScoresAdapter(scores)
        recyclerView = findViewById(R.id.score_scroll)

        recyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

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
     * Retrieves a json from server and converts to Highscore class
     */
    fun apiGetHighscores(): Highscore{
        val res = URL("http://royaltrashapp.azurewebsites.net/api/highscores/1").readText(Charsets.UTF_8).trimStart('[').trimEnd(']')
        val ob = Gson()
        val highScoreUser = ob.fromJson(res, Highscore::class.java)

        return highScoreUser
    }

    data class Highscore(
            @SerializedName("hs_id") val hs_id: Int,
            @SerializedName("hs_username") val hs_username: String,
            @SerializedName("lat") val lat: Float,
            @SerializedName("description") val lng: Float
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
