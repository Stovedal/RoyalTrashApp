package se.quiz.trashers.royaltrash

import android.graphics.Color
import android.os.Bundle
import android.support.design.card.MaterialCardView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.content_score_board.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.support.v7.widget.DividerItemDecoration
import android.view.View
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.vision.text.Line
import kotlinx.android.synthetic.main.score_item.*
import kotlinx.android.synthetic.main.score_item_leader.*
import org.w3c.dom.Text


class ScoreBoardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var scores: ArrayList<DBrequests.Highscore>
    private var show = false
    private var userPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_score_board)
        viewManager = LinearLayoutManager(this)



        val data = getSharedPreferences("Data", 0)
        val username = data!!.getString("Username", null)
        val userlat = data.getString("lat", null)
        val userlng = data.getString("lng", null)
        println("User lat long is $userlat  $userlng")

        all_button.run {
            setBackgroundColor(getColor(R.color.colorAccent))
            setTextColor(Color.WHITE)
        }

        if(findViewById<MaterialCardView>(R.id.show_my_score)!=null){
            println("IN rifnaoi")
        }

        launch {
            scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())
            launch(UI){
                val userposition = scores.indexOf(scores.find { it.hs_username == username })
                viewAdapter = ScoresAdapter(scores, userposition)
                recyclerView = findViewById(R.id.score_scroll)
                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter

                }

                recyclerView.setOnScrollChangeListener({
                    v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()<userposition &&
                            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()>userposition){
                        println("hellooo")
                        score_item_leader.visibility= View.INVISIBLE

                    } else {
                        println("mndp")
                        score_item_leader.visibility= View.VISIBLE
                    }
                })

                (viewManager as LinearLayoutManager).scrollToPositionWithOffset(userposition, 50)
            }

        }



        close_by_button.setOnClickListener {
            val filtered = scores.filter{ it.lat != null }
            val userposition = filtered.indexOf(filtered.find { it.hs_username == username })
            recyclerView.adapter = ScoresAdapter(filtered.toCollection(ArrayList()), userposition)
            (viewManager as LinearLayoutManager).scrollToPositionWithOffset(userposition, 50)
            close_by_button.run {
                setBackgroundColor(getColor(R.color.colorAccent))
                setTextColor(Color.WHITE)
            }
            all_button.run {
                setBackgroundColor(Color.TRANSPARENT)
                setTextColor(getColor(R.color.colorAccent))
            }
        }




        all_button.setOnClickListener {
            recyclerView.adapter.apply {  }
            val userposition = scores.indexOf(scores.find { it.hs_username == username })
            recyclerView.adapter = ScoresAdapter(scores.toCollection(ArrayList()), userposition)
            (viewManager as LinearLayoutManager).scrollToPositionWithOffset(userposition, 50)
            all_button.run {
                setBackgroundColor(getColor(R.color.colorAccent))
                setTextColor(Color.WHITE)
            }
            close_by_button.run {
                setBackgroundColor(Color.TRANSPARENT)
                setTextColor(getColor(R.color.colorAccent))
            }
        }
    }

}
