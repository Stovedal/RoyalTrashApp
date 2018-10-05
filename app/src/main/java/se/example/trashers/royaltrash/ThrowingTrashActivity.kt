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
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.BitmapFactory


class AnimatedObj(var frame:Int = 0,var rows:Int, var columns:Int,var imageID:Int,var width:Int,var height:Int ){
    var save:HashMap<Int,Bitmap>? = null
    var Running = false

    init {
        this.save = HashMap<Int,Bitmap>()
    }

    fun SetSave(save:HashMap<Int,Bitmap>){
        this.save = save
    }
    fun GetNextFrame():Bitmap?{
        if(frame < rows*columns){
            frame++

        }else{
            frame = 0
        }
        if(this.save != null){
            return this.save!![frame]
        }
        return null
    }
    fun isAnimDone():Boolean{
        if (frame == rows*columns){
            frame = 0
            return true
        }else{
            return false
        }
    }
    fun resetFrame(){
        frame = 0
        this.Running = false
    }
    fun startAnim(){
        this.Running = true
    }

}

class ThrowingTrashActivity : AppCompatActivity() {
    //ThrowingTrashIsAFunActivity

    var screanWidth:Int? = null
    var screanHeight:Int? = null
    var currentScore:Int = 0
    var activeTrash:Trash? = null
    var fromOnCreat:Boolean = false
    val CanSetup = hashMapOf<Int,String>()
    var killingSpree = 0 //xD
    var LongestkillingSpree = 0 //xD
    var constraintLayout:ConstraintLayout? =  null
    var TimeLeft = 25
    var QuizScore = 0;
    var starAnim = AnimatedObj(0,4,5,R.drawable.star_sprite,256,256)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwing_trash)
        QuizScore = intent.getIntExtra("score", 0)
        TimeLeft += QuizScore


        fromOnCreat = true

        constraintLayout = findViewById(R.id.playfield) as ConstraintLayout


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
                addImage()
            }
            true
        })
        dragable_test.setOnTouchListener(listener)
    }




    /*
    * ##################################
    * Sprite animation methods start here
    * #############################
    * */
    fun CreatanimateSpriteImages(obje:AnimatedObj):AnimatedObj{
        //fun CreatanimateSpriteImages(rows:Int,columns:Int,width:Int,height:Int){
        var save = HashMap<Int,Bitmap>()
        val options = BitmapFactory.Options()
        options.inScaled = false
        var bm: Bitmap?= BitmapFactory.decodeResource(getResources(), obje.imageID, options)
        var cframe = 0
        for(i in 0 until obje.rows){
            for(j in 0 until obje.columns) {
                val bmap = Bitmap.createBitmap(obje.width, obje.height, Bitmap.Config.ARGB_8888)
                val c = Canvas(bmap)
                val frame = Rect((j*obje.width), (i*obje.height), (j*obje.width)+obje.width, (i*obje.height)+obje.height)
                val dst = Rect(0, 0, obje.width, obje.height)
                c.drawBitmap(bm, frame, dst, null)
                save[cframe] = bmap
                cframe ++
            }
        }
        obje.SetSave(save)
        return obje
    }

    fun startAnimateimg(obje:AnimatedObj){
        if (obje.frame == 0){
            obje.startAnim()
        }
        var Nframe = obje.GetNextFrame()
        if(Nframe != null) {
            animation_holder.setImageBitmap(Nframe)
        }
        if(!obje.isAnimDone() && obje.Running){
            Handler().postDelayed(
                    Runnable {
                        if (obje.Running) {
                            startAnimateimg(obje)
                        }
                    },
                    20)
        }
    }

    /*
    * ##############################
    * Sprite animation methods ends here
    * ##################################
    * */



    public override fun onResume() {
        super.onResume()
        if (fromOnCreat){
            fromOnCreat = false
            //this is so fucking ugly, but moving objects just won't work untill the screan HAVE BEEN loaded for some ms
            Handler().postDelayed(Runnable { setObjectPercentLocation("dragable_test",45F,80F) }, 100)
            TimerCountDown()
            starAnim = CreatanimateSpriteImages(starAnim)
            Handler().postDelayed(Runnable { startAnimateimg(starAnim) }, 400)
        }
    }

    fun TimerCountDown(){
        TimeLeft -= 1
        timer_text.text = TimeLeft.toString()
        if(TimeLeft > 0) {
            Handler().postDelayed(
                    Runnable {
                        TimerCountDown()
                    },
                    1000)
        }else{
            //go to summary screen!
            val intent = Intent(this, QuizSummary::class.java)
            intent.putExtra("Score",currentScore)
            intent.putExtra("QuizScore",QuizScore)
            intent.putExtra("killingSpree",LongestkillingSpree)
            startActivity(intent)
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
        val ProsX = ((target.x+ (target.width/2))/screanWidth!!)*100
        val ProsY = ((target.y+ (target.height/2))/screanHeight!!)*100
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
        target.x = (Xcord/100)*screanWidth!! - target.width/2
        target.y = (Ycord/100)*screanHeight!! - target.height/2
        println("Setting Xpos: "+target.x +"Setting Ypos: "+target.y)

    }
    fun setObjectPercentLocation(id: Int,Xcord: Float,Ycord: Float){

        val target = findViewById(id) as ImageView
        target.x = (Xcord/100)*screanWidth!! - target.width/2
        target.y = (Ycord/100)*screanHeight!! - target.height/2
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

    fun addImage(){
        /*var imageView = ImageView(this)
        var mID = imageView.id
        imageView.setImageResource(R.drawable.crown)
        constraintLayout!!.addView(imageView)
        setObjectPercentLocation(mID,50F,50F)
        */


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
        val newTrash = Trash(random!!,this)
        return newTrash
    }

    fun updateStreak(status:Boolean){
        if(status){
            killingSpree++;
        }else{
            if (LongestkillingSpree < killingSpree){
                LongestkillingSpree = killingSpree
            }
            killingSpree = 0
        }
        streak.text = "Streak:" + (killingSpree).toString()

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

                var Tpos = getObjectPosInPercent("can_" + i.toString())
                setObjectPercentLocation("animation_holder",Tpos.first,Tpos.second)

                starAnim.resetFrame()
                startAnimateimg(starAnim)

                activeTrash = getNewTrash()//update the trash selected
                changeObjectIcon("dragable_test",activeTrash!!)
                setObjectPercentLocation("dragable_test",45F,80F)
                collided = true
                updateStreak(true)
                break;
            }
        }
        if(!collided){
            setObjectPercentLocation("dragable_test",70F,70F)
            updateStreak(false)
        }
    }
}
