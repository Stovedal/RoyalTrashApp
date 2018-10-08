package se.example.trashers.royaltrash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_summary.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlin.math.roundToInt


class QuizSummary : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_summary)
        val Score :Int = intent.getIntExtra("Score", 0)//trash score
        val QuizScore :Int = intent.getIntExtra("QuizScore", 0)//quiz score
        val killingSpree :Int = intent.getIntExtra("killingSpree", 0)//longest trash streak
        val FScore = ((Score + QuizScore) * (1 + (0.1F*killingSpree))).roundToInt()
        button_accept.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        button_accept.isEnabled = false
        PostResulr(FScore)
        calc.text = "(score+trashScore)*(longeststreak*0.1)"
        final_score.text = "Score:" + FScore.toString()
    }
    fun PostResulr(Result:Int){
        val data = getSharedPreferences("Data", 0)
        var Username = data!!.getString("Username", null)
        if(Username != null) {
            var Scores: Array<DBrequests.Highscore>? = null
            Username = Username.replace("[^A-Za-z0-9]+".toRegex(), "").toLowerCase()
            launch {
                try {
                    Scores = DBrequests().apiGetHighscoreByUsername(Username)
                } catch (e: Exception) {
                    println("ERROR in db connection (GET): " + e)
                }
                if(Scores!![0] != null){
                    if(Scores!![0].hs_score < Result){
                        val PostUser = hashMapOf("hs_id" to Scores!![0].hs_id, "hs_username" to Username, "hs_score" to Result, "lat" to 0, "lng" to 0)
                        val JsonStr = Gson().toJson(PostUser)
                        try {
                            DBrequests().apiSetHighscoreByUsername("http://royaltrashapp.azurewebsites.net/api/Highscores/PutHighscore/"+ (Scores!![0].hs_id).toString(), JsonStr)
                        }catch (g: Exception){
                            println("ERROR in db connection (PUT): " + g)
                        }
                    }
                }
                try {
                    launch(UI) {
                        button_accept.isEnabled = true
                    }
                }catch (f:Exception){
                    println("ERROR in UI launcher thread!: " + f)

                }
            }
        }
    }
}
