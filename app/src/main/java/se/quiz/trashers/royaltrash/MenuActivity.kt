package se.quiz.trashers.royaltrash

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity(),LoginDialogFragment.FragmentCommunication {

    private var data: SharedPreferences? = null
    private var DisplayingFragment = false
    private var Version = 45//hehe
    private var eggsCraked = 0

    //Location stuffs
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val rEQUEST_CODE = 1000;

    private fun versionCheck(){
        data = getSharedPreferences("Data", 0)
        val cVersion = data!!.getInt("Version", -1)
        if(cVersion != Version){
            resetapp()
            data!!.edit().putInt("Version", Version).commit()
        }
    }


    var starAnim2 = AnimatedObj(0, 7, 8, R.drawable.egg,270 , 313)
    var starAnim = AnimatedObj(0, 4, 5, R.drawable.trashyrotate_sprite4, 338, 480)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)



        //var starAnim2 = AnimatedObj(0, 7, 8, R.drawable.egg,270 , 312)
        //var starAnim = AnimatedObj(0, 4, 5, R.drawable.trashyrotate_sprite4, 338, 480)

        data = getSharedPreferences("Data", 0)
        data!!.edit().putInt("Eggs", 0).commit()

        starAnim2 = ThrowingTrashActivity().CreatanimateSpriteImages(starAnim2, getResources())
        //ThrowingTrashActivity().startAnimateimg(starAnim2, animation_holder3)

        starAnim = ThrowingTrashActivity().CreatanimateSpriteImages(starAnim, getResources())
        ThrowingTrashActivity().startAnimateimg(starAnim, animation_holder2)

        animation_holder3.setImageDrawable(null)
        animation_holder2.setOnClickListener {
            eggsCraked ++
            if(eggsCraked < 8){
                ThrowingTrashActivity().startAnimateimg(starAnim, animation_holder2)
                animation_holder3.setImageDrawable(null)
            }else{
                data = getSharedPreferences("Data", 0)
                data!!.edit().putInt("Eggs", eggsCraked).commit()
                animation_holder2.setImageDrawable(null)
                mainmenu.setBackgroundResource(R.drawable.bg_egg)
                start_button.setBackgroundResource(R.drawable.button_egg)
                highscores_button.setBackgroundResource(R.drawable.button_egg)
                highscores_button.setTextColor(Color.WHITE)
                start_button.setTextColor(Color.WHITE)
                usernamefield.bringToFront()
                usernamefield.setTextColor(Color.BLACK)
                ThrowingTrashActivity().startAnimateimg(starAnim2, animation_holder3)
            }


        }



        start_button.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("questionNumber", 30)
            startActivity(intent)
        }

        highscores_button.setOnClickListener {
            val intent = Intent(this, ScoreBoardActivity::class.java)
            startActivity(intent)
        }

        //ENABLE this to reset the username att app startup
        versionCheck()

        LoadApp()

        //Location
        //Check permissions
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), rEQUEST_CODE)
        }
        else
        {
            buildLocationRequest()
            buildLocationCallback()

            //Create FusedProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            //Check permissions
            if(ActivityCompat.checkSelfPermission(this@MenuActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this@MenuActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this@MenuActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), rEQUEST_CODE)
                return
            }
            //Get Location

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())

        }

    }



    private fun buildLocationCallback(){
        locationCallback = object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                launch() {
                    val location_ = p0!!.locations.get(p0!!.locations.size-1) //Get last location
                    val username = data!!.getString("Username", null)
                    try {
                        val user = DBrequests().apiGetHighscoreByUsername(username)[0]

                        val PostUser = hashMapOf("hs_Id" to user.hs_id, "hs_username" to user.hs_username, "hs_score" to user.hs_score, "lat" to location_.latitude, "lng" to location_.longitude)
                        val JsonStr = Gson().toJson(PostUser)
                        try {
                            DBrequests().apiSetHighscoreByUsername(user.hs_id.toString(), JsonStr)
                        } catch (g: Exception) {
                            println("MENU ACTIVITY ERROR in db connection (PUT): " + g)
                        }
                        val editor = getSharedPreferences("Data", 0).edit()
                        editor.putString("lat", location_.latitude.toString())
                        editor.putString("lng", location_.longitude.toString())
                        editor.apply()
                        println("PRINTING LOCATION:" + location_.toString())
                    } catch (e:ArrayIndexOutOfBoundsException) {
                        usernameFragment()
                    }

                }
            }
        }

    }

    private fun buildLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }


    override fun fragmentCommunicationSetUsername(Username:String) {
        println("Setting username!")
        val editor = data!!.edit()
        editor.putString("Username", Username)
        editor.commit()
        DisplayingFragment = false
        this.runOnUiThread { LoadApp() }
    }


    fun resetapp(){
        data = getSharedPreferences("Data", 0)
        data!!.edit().clear().commit()

    }


    fun LoadApp(){
        data = getSharedPreferences("Data", 0)

        val Username = data!!.getString("Username", null)
        if(Username != null){
            //set username
            usernamefield.text = Username
        }else{
            //oppen name fragment
            usernameFragment()
        }
    }

    fun usernameFragment() {
        DisplayingFragment = true
        val ft = getSupportFragmentManager().beginTransaction()
        val newFragment = LoginDialogFragment.newInstance("placeholder...")
        newFragment.isCancelable = false
        newFragment.show(ft, "dialog")
    }

    var ClickTime = 0L
    override fun onBackPressed() {
        //ClickTime = System.currentTimeMillis();
        var Tmp_Time = System.currentTimeMillis();
        if(Tmp_Time-ClickTime < 2000L){
            finish();
            System.exit(0);
        }else{
            Toast.makeText(this, "tryck bakåt igen för att avsluta appen", Toast.LENGTH_SHORT).show()
            ClickTime = System.currentTimeMillis();
        }
        //fragment are not displayed use standard back behavior
    }
}

