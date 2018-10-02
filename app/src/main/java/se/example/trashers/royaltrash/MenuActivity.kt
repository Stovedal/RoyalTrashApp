package se.example.trashers.royaltrash

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity(),LoginDialogFragment.fragmentComunication  {

    private var data: SharedPreferences? = null
    private var DisplayingFragment = false
    private var Version = 2//hehe


    fun versionChek(){
        data = getSharedPreferences("Data", 0)
        val CVersion = data!!.getInt("Version", -1)
        if(CVersion != Version){
            resetapp()
            data!!.edit().putInt("Version", Version).commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        start_button.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("questionNumber", 3)
            startActivity(intent)
        }

        //ENABLE this to reset the username att app startup
        //resetapp()
        versionChek()

        LoadeApp()

        highscores_button.setOnClickListener {
            val intent = Intent(this, ScoreBoardActivity::class.java)
            startActivity(intent)
        }
    }



    override fun fragmentComunicationSetUsername(Username:String) {
        println("Setting username!")
        val editor = data!!.edit()
        editor.putString("Username", Username)
        editor.commit()
        DisplayingFragment = false
        this.runOnUiThread { LoadeApp() }
    }


    fun resetapp(){
        data = getSharedPreferences("Data", 0)
        data!!.edit().clear().commit()

    }


    fun LoadeApp(){
        data = getSharedPreferences("Data", 0)

        val Username = data!!.getString("Username", null)
        if(Username != null){
            //set username
            usernamefield.text = Username
        }else{
            //oppen name fragment
            DisplayingFragment = true
            val ft = getSupportFragmentManager().beginTransaction()
            val newFragment = LoginDialogFragment.newInstance("placeholder...")
            newFragment.isCancelable = false
            newFragment.show(ft, "dialog")
        }
    }

    override fun onBackPressed() {
        if (DisplayingFragment){
            //fragment is displayed, do nothing!
            println("fragment displayed")
        }else{
            super.onBackPressed();
            //fragment are not displayed use standard back behavior
        }
    }
}

