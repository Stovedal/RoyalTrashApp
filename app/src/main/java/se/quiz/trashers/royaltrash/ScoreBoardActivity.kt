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
import android.widget.*
import android.widget.LinearLayout.HORIZONTAL
import com.google.android.gms.vision.text.Line
import kotlinx.android.synthetic.main.score_item.*
import kotlinx.android.synthetic.main.score_item_leader.*
import org.w3c.dom.Text
import java.lang.Math.abs

/**
 * Activity controlling the highscore-view.
 *
 */

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


        /**
         * Get data about the user
         */
        val data = getSharedPreferences("Data", 0)
        val username = data!!.getString("Username", null)
        val userlat = data.getString("lat", null)
        val userlng = data.getString("lng", null)



        launch {
            /**
             * Get scores from  database
             * and fill the recycleview.
             */
            scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())
            score_radius.isEnabled = true;

            launch(UI){
                var userposition = scores.indexOf(scores.find { it.hs_username == username })
                viewAdapter = ScoresAdapter(scores, userposition)
                recyclerView = findViewById(R.id.score_scroll)
                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter

                }

                //Create the scroll-to-your-score button.
                score_item_leader.findViewById<TextView>(R.id.down_text).text = scores.get(userposition).hs_username
                score_item_leader.findViewById<TextView>(R.id.score_count).text = scores.get(userposition).hs_score.toString() + 'p'
                recyclerView.setOnScrollChangeListener({
                    v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()<=userposition &&
                            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()>=userposition){
                        score_item_leader.visibility= View.INVISIBLE
                    } else if (userposition==0){
                        score_item_leader.visibility= View.INVISIBLE
                    } else {
                        score_item_leader.visibility= View.VISIBLE
                        if((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()<userposition){
                            ic_down.rotation= 0f
                        } else {
                            ic_down.rotation= 180f
                        }
                    }
                })

                score_item_leader.setOnClickListener{
                    if((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() < userposition){
                        recyclerView.smoothScrollToPosition(userposition+2)
                    } else {
                        recyclerView.smoothScrollToPosition(userposition-2)
                    }
                }

                //Create and set handles for the slider which controls which scores to be filtered out
                if(userlat!=null && userlng!=null){
                    score_radius_text.text = "Visar toppspelare inom : 100 km radie"
                    score_radius.setProgress(100);
                    score_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            println("hej") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            println("hej") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                            // Display the current progress of SeekBar

                            score_radius_text.text = "Visar toppspelare inom : $i km radie"
                            val delta = i.toFloat()*2
                            val newScores = scores.filter{
                                it ->
                                if(it.hs_username == data!!.getString("Username", null)){
                                    true
                                } else if(it.lat!=null && it.lng!=null){
                                    println("lat: " + abs(userlat.toFloat()-it.lat.toFloat()) + " lng: " + abs(userlat.toFloat()-it.lng.toFloat()) + " i: " + delta)
                                    println("lat " + userlat.toFloat() + " - " + it.lat.toFloat() + " is " + abs(userlat.toFloat()-it.lat.toFloat()))
                                    abs(userlat.toFloat()-it.lat.toFloat()) < delta && abs(userlat.toFloat()-it.lng.toFloat()) < delta
                                } else {
                                    false
                                }
                            }

                            userposition = newScores.indexOf(newScores.find { it.hs_username == username })

                            recyclerView.adapter = ScoresAdapter(newScores.toCollection(
                                    ArrayList<DBrequests.Highscore>()
                            ), userposition);
                        }

                    })
                } else {
                    score_radius.isEnabled = false
                }


            }
        }



    }
}
