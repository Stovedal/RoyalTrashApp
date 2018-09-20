package se.example.trashers.royaltrash

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_video_bg.*

class VideoBgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_bg)
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.earth)
        BGVideo.setVideoURI(uri)
        BGVideo.start()
        BGVideo.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer -> mediaPlayer.isLooping = true })

        start_button.setOnClickListener {
            //val intent = Intent(this, QuizActivity::class.java)
            val intent = Intent(this, ThrowingTrashActivity::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }
        start_button2.setOnClickListener {
            //val intent = Intent(this, QuizActivity::class.java)
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("key", "Kotlin")
            startActivity(intent)
        }


    }

    /**
     * restarts the bg video on resume, prevents it from getting stuck
     */
    public override fun onResume() {
        super.onResume()
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.earth)
        BGVideo.setVideoURI(uri)
        BGVideo.start()
        BGVideo.setOnPreparedListener { mediaPlayer -> mediaPlayer.isLooping = true }
    }

}