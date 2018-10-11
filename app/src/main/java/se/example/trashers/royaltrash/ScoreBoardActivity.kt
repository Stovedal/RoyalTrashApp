package se.example.trashers.royaltrash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ScoreBoardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
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
            val scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())
            launch(UI){
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
    }

}
