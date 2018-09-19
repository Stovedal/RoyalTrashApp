package se.example.trashers.royaltrash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_throwing_trash.*
import java.lang.Math.pow

class ThrowingTrashActivity : AppCompatActivity() {
    //ThrowingTrashIsAFunActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwing_trash)


        var listener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                view.y = motionEvent.rawY - view.height/2
                view.x = motionEvent.rawX - view.width/2
                val dist = checkCollitionState()
                println("Dist found: " + dist)
            }
            true
        })

        dragable_test.setOnTouchListener(listener)




    }




    fun checkCollitionState():Double{
        var dist = pow(pow((dragable_test.x - can.x).toDouble(),2.0) + pow((dragable_test.y - can.y).toDouble(),2.0),0.5)
        //dragable_test.x

        return dist
    }














}
