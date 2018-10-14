package se.example.trashers.royaltrash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.content_score_board.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import se.example.trashers.royaltrash.R.id.close_by_button

class ScoreBoardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var scores: ArrayList<DBrequests.Highscore>
    private val userPosition = Int;
    //data class Score(val name: String, val score: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_score_board)
        viewManager = LinearLayoutManager(this)

        val data = getSharedPreferences("Data", 0)
        val username = data!!.getString("Username", null)
        val userlat = data.getString("lat", null)
        val userlng = data.getString("lng", null)
        println("User lat long is $userlat  $userlng")

        launch {
            scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())
            launch(UI){
                for (score in scores){
                    println(score.hs_username + " " + score.lat + " " + score.lng)
                }
                val userposition = scores.indexOf(scores.find { it.hs_username == username })
                viewAdapter = ScoresAdapter(scores, userposition)
                recyclerView = findViewById(R.id.score_scroll)
                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
                (viewManager as LinearLayoutManager).scrollToPositionWithOffset(userposition, 50)

            }
        }

        close_by_button.setOnClickListener {
            val filtered = scores.filter{ it.lat != null }
            val userposition = filtered.indexOf(filtered.find { it.hs_username == username })
            recyclerView.adapter = ScoresAdapter(filtered.toCollection(ArrayList()), userposition)
            (viewManager as LinearLayoutManager).scrollToPositionWithOffset(userposition, 50)
        }

        all_button.setOnClickListener {
            recyclerView.adapter.apply {  }
            val userposition = scores.indexOf(scores.find { it.hs_username == username })
            recyclerView.adapter = ScoresAdapter(scores.toCollection(ArrayList()), userposition)
            (viewManager as LinearLayoutManager).scrollToPositionWithOffset(userposition, 50)
        }
    }

}
