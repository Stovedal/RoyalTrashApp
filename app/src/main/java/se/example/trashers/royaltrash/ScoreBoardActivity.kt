package se.example.trashers.royaltrash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.json.JSONObject
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
    private fun connectToAPI(){
        val result = URL("http://royaltrashapp.azurewebsites.net/api/highscores1/1/").readText()


    }


    data class Highscore(val id: Long,
                          val username: String, val score: Long,
                          val trashcount: String, val City: String)


}
