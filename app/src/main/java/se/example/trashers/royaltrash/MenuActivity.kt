package se.example.trashers.royaltrash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.ProxyFileDescriptorCallback
import android.support.multidex.MultiDex
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity(),LoginDialogFragment.fragmentComunication {


    private var data: SharedPreferences? = null
    private var DisplayingFragment = false
    private var Version = 4//hehe

    //Location stuffs
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE = 1000;

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

        //Location
        //Check permissions
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else
        {
            buildLocationRequest()
            buildLocationCallback()

            //Create FusedProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


            //Set event
            location.setOnClickListener( View.OnClickListener {
                if(ActivityCompat.checkSelfPermission(this@MenuActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@MenuActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this@MenuActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
                    return@OnClickListener
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
            })

            //Change State of button


        }
        //Location end


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

    //Locationfunctions

    private fun buildLocationCallback(){
        locationCallback = object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                val location_ = p0!!.locations.get(p0!!.locations.size-1) //Get last location
                location.text = location_.latitude.toString() + "/" + location_.longitude.toString()
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

    //Locationfunctions end



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
        } else {
            super.onBackPressed();
            //fragment are not displayed use standard back behavior
        }
    }
}

