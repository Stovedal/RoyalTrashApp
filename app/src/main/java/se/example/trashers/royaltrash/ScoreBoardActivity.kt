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
            val scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())



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
