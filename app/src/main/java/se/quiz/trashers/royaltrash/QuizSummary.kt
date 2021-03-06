package se.quiz.trashers.royaltrash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_summary.*
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch

class QuizSummary : AppCompatActivity() {

    private var impHighscore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_summary)
        val score :Int = intent.getIntExtra("Score", 0)//trash score
        val quizScore :Int = intent.getIntExtra("QuizScore", 0)//quiz score
        val killingSpree :Int = intent.getIntExtra("killingSpree", 0)//longest trash streak
        val fscore = ((quizScore * 5) + score * (1 + killingSpree))
        button_accept.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        button_accept.isEnabled = false
        postResult(fscore)
        calc.text = " Quiz Score: $quizScore\n Trash Score: $score \n Streak: $killingSpree"//"(score+trashScore)*(longeststreak*0.1)"
        final_score.text = getString(R.string.final_score, fscore)
        val data = getSharedPreferences("Data", 0)
        val username = data!!.getString("Username", null)
        txtViewUser.text = getString(R.string.brajobbat, username)

    }
    private fun postResult(Result:Int){
        val data = getSharedPreferences("Data", 0)
        var Username = data!!.getString("Username", null)
        if(Username != null) {
            var scores: Array<DBrequests.Highscore>? = null
            Username = Username.replace("[^A-Za-z0-9]+".toRegex(), "").toLowerCase()
            launch {
                try {
                    scores = DBrequests().apiGetHighscoreByUsername(Username)
                } catch (e: Exception) {
                    println("ERROR in db connection (GET): $e")
                }
                if(scores!![0] != null){
                    if(scores!![0].hs_score < Result){
                        impHighscore = true
                        launch(UI) {
                            txtViewUser.text = getString(R.string.nytthighscore, Username)
                            imageView.setImageDrawable(getDrawable(R.drawable.trashy_1st))
                            imageView.setImageResource(R.drawable.trashy_1st)
                        }
                        val postUser = hashMapOf("hs_id" to scores!![0].hs_id, "hs_username" to Username, "hs_score" to Result, "lat" to 0, "lng" to 0)
                        val jsonStr = Gson().toJson(postUser)
                        try {
                            DBrequests().apiSetHighscoreByUsername((scores!![0].hs_id).toString(), jsonStr)
                        }catch (g: Exception){
                            println("ERROR in db connection (PUT): $g")
                        }
                    }
                }
                try {
                    launch(UI) {
                        button_accept.isEnabled = true
                    }
                }catch (f:Exception){
                    println("ERROR in UI launcher thread!: $f")

                }
            }
        }
    }
    var ClickTime = 0L
    override fun onBackPressed() {
        var Tmp_Time = System.currentTimeMillis();
        if(Tmp_Time-ClickTime < 2000L){
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "tryck bakåt igen för att avsluta", Toast.LENGTH_SHORT).show()
            ClickTime = System.currentTimeMillis();
        }
    }
}
