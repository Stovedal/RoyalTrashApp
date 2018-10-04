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
        println("HALlLEOAOEÅ ")
        viewManager = LinearLayoutManager(this)

        val data = getSharedPreferences("Data", 0)
        val Username = data!!.getString("Username", null)

        launch {
            val scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())
            launch(UI){
                val user_position = scores.indexOf(scores.find { it.hs_username == Username })
                viewAdapter = ScoresAdapter(scores, user_position)
                recyclerView = findViewById(R.id.score_scroll)
                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
                (viewManager as LinearLayoutManager).scrollToPositionWithOffset(user_position, 50)

            }
        }
    }

}
