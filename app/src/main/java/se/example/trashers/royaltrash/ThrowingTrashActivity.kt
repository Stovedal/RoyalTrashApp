package se.example.trashers.royaltrash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_throwing_trash.*
import java.lang.Math.pow
import java.util.*
import android.animation.ObjectAnimator
import android.os.Handler



class ThrowingTrashActivity : AppCompatActivity() {
    //ThrowingTrashIsAFunActivity

    var screanWidth:Int? = null
    var screanHeight:Int? = null
    var currentScore:Int = 0
    var activeTrash:Trash? = null
    var fromOnCreat:Boolean = false
    val CanSetup = hashMapOf<Int,String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwing_trash)
        fromOnCreat = true
        if(screanWidth == null) {
            setScreanSize();
            setTrashCaregory();
            setupTrashcanIcons();
            activeTrash = getNewTrash()
            changeObjectIcon("dragable_test",activeTrash!!)
        }

        var listener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                view.y = motionEvent.rawY - view.height/2
                view.x = motionEvent.rawX - view.width/2
            }else if(motionEvent.action == MotionEvent.ACTION_UP){
                checkCollitionState()
            }
            true
        })
        dragable_test.setOnTouchListener(listener)
    }

    public override fun onResume() {
        super.onResume()
        if (fromOnCreat){
            fromOnCreat = false
            //this is so fucking ugly, but moving objects just won't work untill the screan HAVE BEEN loaded for some ms
            Handler().postDelayed(Runnable { setObjectPercentLocation("dragable_test",45F,80F) }, 100)
        }
    }

    fun setTrashCaregory(){
        val Cat = mutableListOf("GLASS","PAPER","ORGANIC","EWASTE","METAL","PLASTIC")
        Cat.shuffle()
        for (i in 1..3){
            CanSetup[i] = Cat[i]
        }
    }

    fun setScreanSize(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screanWidth = displayMetrics.widthPixels
        screanHeight = displayMetrics.heightPixels
    }

    fun limitAtEdges(){
        if (dragable_test.x <= 0){
            dragable_test.x = 1.0F
        }
        if (dragable_test.y <= 0){
            dragable_test.y = 1.0F
        }
        if (dragable_test.y > screanHeight!!){
            dragable_test.y = screanHeight!!.toFloat()
        }
        if (dragable_test.x > screanWidth!!){
            dragable_test.x = screanWidth!!.toFloat()
        }
    }

    /*
    get procentage on screen of given ImageView ID
    NOT!: id must be of an ImageView!
     */
    fun getObjectPosInPercent(targetVievID: String): Pair<Float,Float>{

        val id: Int = getResources().getIdentifier(targetVievID, "id", getPackageName())
        val target = findViewById(id) as ImageView
        val ProsX = (target.x/screanWidth!!)*100
        val ProsY = (target.y/screanHeight!!)*100
        return Pair(ProsX,ProsY)
    }

    /*
        get distance betwen targetVievID and secoundTargetVievID in a normalized value based of percentage
     */
    fun getDistanceinPercent(targetVievID: String,secoundTargetVievID: String):Double{
        val Pros_1 = getObjectPosInPercent(targetVievID)
        val Pros_2 = getObjectPosInPercent(secoundTargetVievID)
        var distPros = pow(pow((Pros_1.first - Pros_2.first).toDouble(),2.0) + pow((Pros_1.second - Pros_2.second).toDouble(),2.0),0.5)
        return distPros
    }

    /*
        get distance betwen targetVievID and secoundTargetVievID in pixels
     */
    fun getDistanceInPixels(targetVievID: String,secoundTargetVievID: String):Double{
        val id: Int = getResources().getIdentifier(targetVievID, "id", getPackageName())
        val target = findViewById(id) as ImageView
        val secoundTarget_id: Int = getResources().getIdentifier(secoundTargetVievID, "id", getPackageName())
        val secound_target = findViewById(secoundTarget_id) as ImageView
        var dist = pow(pow((target.x - secound_target.x).toDouble(),2.0) + pow((target.y - secound_target.y).toDouble(),2.0),0.5)
        return dist
    }

    fun setObjectPixelLocation(targetVievID: String,Xcord: Float,Ycord: Float){
        val id: Int = getResources().getIdentifier(targetVievID, "id", getPackageName())
        val target = findViewById(id) as ImageView
        target.x = Xcord;
        target.y = Ycord;
    }

    fun setObjectPercentLocation(targetVievID: String,Xcord: Float,Ycord: Float){
        val id: Int = getResources().getIdentifier(targetVievID, "id", getPackageName())
        val target = findViewById(id) as ImageView
        target.x = (Xcord/100)*screanWidth!!
        target.y = (Ycord/100)*screanHeight!!
        println("Setting Xpos: "+target.x +"Setting Ypos: "+target.y)

    }

    fun increaseScore(){
        currentScore ++
        score.text = "Score:" + (currentScore).toString()
    }


    fun shakeIcon(targetVievID: String){
        val id: Int = getResources().getIdentifier(targetVievID, "id", getPackageName())
        val target = findViewById(id) as ImageView
        val rotate = ObjectAnimator.ofFloat(target, "rotation", 0f, 15f, 0f, -15f, 0f)
        // repeat the loop 6 times
        rotate.repeatCount = 6
        // animation play time 120 ms
        rotate.duration = 120
        rotate.start()
    }

    fun setupTrashcanIcons(){
        val CanIcons = hashMapOf<String,String>("GLASS" to "glassortering","PAPER" to "papperssortering",
                "ORGANIC" to "organisksortering","EWASTE" to "elektroniksortering",
                "METAL" to "metallsortering","PLASTIC" to "plastsortering")
        can_1.setImageResource(getResources().getIdentifier(CanIcons[CanSetup[1]], "drawable", getPackageName()))
        can_2.setImageResource(getResources().getIdentifier(CanIcons[CanSetup[2]], "drawable", getPackageName()))
        can_3.setImageResource(getResources().getIdentifier(CanIcons[CanSetup[3]], "drawable", getPackageName()))
    }

    fun changeObjectIcon(targetVievID: String,trashObj:Trash){
        val id: Int = getResources().getIdentifier(targetVievID, "id", getPackageName())
        val target = findViewById(id) as ImageView
        val ImgId = getResources().getIdentifier(trashObj.TrashIcon, "drawable", getPackageName())
        target.setImageResource(ImgId)
    }

    /*
     Returns a random element.
     */
    fun <L> List<L>.random(): L? = if (size > 0) get(Random().nextInt(size)) else null

    /*
    get a new trash object
     */
    fun getNewTrash():Trash{
        val tsh: MutableList<String> = mutableListOf(CanSetup[1]!!,CanSetup[2]!!,CanSetup[3]!!)
        val random = tsh.random()
        val newTrash = Trash(random!!)
        return newTrash
    }

    fun checkCollitionState(){
        limitAtEdges()
        var collided = false
        for (i in 1..3) {
            var DistToCan = getDistanceinPercent("dragable_test","can_" + i.toString())
            if(DistToCan < 12 && CanSetup[i] == activeTrash!!.trashTyp){
                //user released trash on the can
                increaseScore()
                shakeIcon("can_" + i.toString())
                activeTrash = getNewTrash()//update the trash selected
                changeObjectIcon("dragable_test",activeTrash!!)
                setObjectPercentLocation("dragable_test",45F,80F)
                collided = true
                break;
            }
        }
        if(!collided){
            setObjectPercentLocation("dragable_test",70F,70F)
        }
    }
}
