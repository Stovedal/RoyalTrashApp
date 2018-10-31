package se.quiz.trashers.royaltrash

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.card.MaterialCardView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.content_score_board.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.support.v7.widget.DividerItemDecoration
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.LinearLayout.HORIZONTAL
import com.google.android.gms.vision.text.Line
import kotlinx.android.synthetic.main.score_item.*
import kotlinx.android.synthetic.main.score_item_leader.*
import org.w3c.dom.Text
import java.lang.Math.abs


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


        launch {
            scores =  DBrequests().apiGetHighscores().toCollection(ArrayList())
            launch(UI){
                var userposition = scores.indexOf(scores.find { it.hs_username == username })
                viewAdapter = ScoresAdapter(scores, userposition)
                recyclerView = findViewById(R.id.score_scroll)
                recyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter

                }

                score_item_leader.findViewById<TextView>(R.id.down_text).text = scores.get(userposition).hs_username
                score_item_leader.findViewById<TextView>(R.id.score_count).text = scores.get(userposition).hs_score.toString() + 'p'
                recyclerView.setOnScrollChangeListener({
                    v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()<userposition &&
                            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()>userposition){
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

                if(userlat!=null && userlng!=null){
                    score_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            println("hej") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            println("hej") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                            // Display the current progress of SeekBar

                            score_radius_text.text = "Progress : $i"
                            val delta = i.toFloat()
                            val newScores = scores.filter{
                                it ->
                                if(it.hs_username == data!!.getString("Username", null)){
                                    true
                                } else if(it.lat!=null && it.lng!=null){
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
                    map_button.setOnClickListener{
                        // Initialize a new layout inflater instance
                        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                        // Inflate a custom view using layout inflater
                        val view = inflater.inflate(R.layout.map_view,null)

                        // Initialize a new instance of popup window
                        val popupWindow = PopupWindow(
                                view, // Custom view to show in popup window
                                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
                        )

                        // Set an elevation for the popup window
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            popupWindow.elevation = 10.0F
                        }


                        // If API level 23 or higher then execute the code
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            // Create a new slide animation for popup window enter transition
                            val slideIn = Slide()
                            slideIn.slideEdge = Gravity.TOP
                            popupWindow.enterTransition = slideIn

                            // Slide animation for popup window exit transition
                            val slideOut = Slide()
                            slideOut.slideEdge = Gravity.RIGHT
                            popupWindow.exitTransition = slideOut

                        }

                        // Get the widgets reference from custom view
                        val tv = view.findViewById<TextView>(R.id.text_view)
                        val buttonPopup = view.findViewById<Button>(R.id.button_popup)

                        // Set click listener for popup window's text view
                        tv.setOnClickListener{
                            // Change the text color of popup window's text view
                            tv.setTextColor(Color.RED)
                        }

                        // Set a click listener for popup's button widget
                        buttonPopup.setOnClickListener{
                            // Dismiss the popup window
                            popupWindow.dismiss()
                        }

                        // Set a dismiss listener for popup window
                        popupWindow.setOnDismissListener {
                            Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
                        }


                        // Finally, show the popup window on app
                        TransitionManager.beginDelayedTransition(findViewById(R.id.score_scroll))
                        popupWindow.showAtLocation(
                                findViewById(R.id.score_scroll), // Location to display popup window
                                Gravity.TOP, // Exact position of layout to display popup
                                0, // X offset
                                findViewById<RecyclerView>(R.id.score_scroll).y.toInt() // Y offset
                        )
                    }

                } else {
                    score_radius.isEnabled = false
                }


            }
        }



    }
}
