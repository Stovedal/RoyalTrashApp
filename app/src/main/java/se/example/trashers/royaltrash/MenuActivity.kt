package se.example.trashers.royaltrash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        start_button.setOnClickListener {
            //val intent = Intent(this, QuizActivity::class.java)
            val intent = Intent(this, ThrowingTrashActivity::class.java)
            startActivity(intent)
        }

        alt_menu_button.setOnClickListener {
            //val intent = Intent(this, QuizActivity::class.java)
            val intent = Intent(this, VideoBgActivity::class.java)
            startActivity(intent)
        }

        highscores_button.setOnClickListener {
            val intent = Intent(this, ScoreBoardActivity::class.java)
            startActivity(intent)
        }
    }

}

