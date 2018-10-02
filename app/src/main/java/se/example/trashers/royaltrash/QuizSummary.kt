package se.example.trashers.royaltrash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_summary.*
import kotlinx.android.synthetic.main.fragment_login_dialog.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlin.math.round
import kotlin.math.roundToInt


class QuizSummary : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_summary)
        val Score :Int = intent.getIntExtra("Score", 0)
        val QuizScore :Int = intent.getIntExtra("QuizScore", 0)
        val killingSpree :Int = intent.getIntExtra("killingSpree", 0)

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

        println("Started thread..")
        val data = getSharedPreferences("Data", 0)
        var Username = data!!.getString("Username", null)

        if(Username != null) {
            var Scores: Array<ScoreBoardActivity.Highscore>? = null
            var FoundUser = false
            Username = Username.replace("[^A-Za-z0-9]+".toRegex(), "").toLowerCase()
            launch {
                try {
                    Scores = DBrequests().apiGetHighscores()
                } catch (e: Exception) {
                    println("ERROR in db connection (GET): " + e)
                }
                var BsetScore = 0
                if (Scores != null) {
                    for (itm in Scores!!) {
                        if (itm.hs_username.toLowerCase() == Username) {
                            FoundUser = true
                            if (itm.hs_score>BsetScore){
                                BsetScore = itm.hs_score
                            }
                        }
                    }
                } else {
                    println("WARNING! NO data from server!")
                }
                //got user highscore
                println("Scores: "+ Result+ " Best " + BsetScore)
                if(Result > BsetScore){
                    //new highscore!
                    println("New highscore!")
                    val PostUser = hashMapOf("hs_username" to Username, "hs_score" to Result)
                    val JsonStr = Gson().toJson(PostUser)
                    try {
                        DBrequests().apiHttpPostToServer("http://royaltrashapp.azurewebsites.net/api/highscores", JsonStr)
                    }catch (g: Exception){
                        println("ERROR in db connection (POST): " + g)
                    }
                }
                try {
                    launch(UI) {
                        button_accept.isEnabled = true
                    }
                }catch (f:Exception){
                    println("ERROR in UI launcher thread!: " + f)

                }
                println("thread done..")
            }
        }
    }

}
