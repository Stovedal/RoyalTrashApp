package se.quiz.trashers.royaltrash

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
import android.content.Intent
import android.content.res.Resources
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.BitmapFactory
import android.widget.Toast
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


class AnimatedObj(var frame:Int = 0,var rows:Int, var columns:Int,var imageID:Int,var width:Int,var height:Int ){
    private var save:HashMap<Int,Bitmap>? = null
    var running = false

    init {
        this.save = HashMap<Int,Bitmap>()
    }

    fun setSave(save:HashMap<Int,Bitmap>){
        this.save = save
    }
    fun getNextFrame():Bitmap?{
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
        this.running = false
    }
    fun startAnim(){
        this.running = true
    }

}

class ThrowingTrashActivity : AppCompatActivity(),PauseDialogFragment.FragmentCommunication {
    //ThrowingTrashIsAFunActivity

    private var screanWidth:Int? = null
    private var screanHeight:Int? = null
    private var currentScore:Int = 0
    private var activeTrash:Trash? = null
    private var fromOnCreat:Boolean = false
    private var IsStoped:Boolean = false
    private val CanSetup = hashMapOf<Int,String>()
    private var killingSpree = 0 //xD
    private var longestkillingSpree = 0 //xD
    private var constraintLayout:ConstraintLayout? =  null
    private var timeLeft = 10
    private var QuizScore = 0
    private var TimeHandler = Handler()
    private var starAnim = AnimatedObj(0,4,5,R.drawable.star_sprite,256,256)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throwing_trash)
        QuizScore = intent.getIntExtra("score", 0)
        timeLeft += QuizScore
        DisplayInstructions(timeLeft.toString())
        //countController();
        fromOnCreat = true

        constraintLayout = findViewById<ConstraintLayout>(R.id.playfield)


        if(screanWidth == null) {
            setScreanSize()
            setTrashCaregory()
            setupTrashcanIcons()
            activeTrash = getNewTrash()
            changeObjectIcon("dragable_test",activeTrash!!)
        }

        val listener = View.OnTouchListener(function = { view, motionEvent ->
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

    override fun fragmentCommunicationStart() {
        countController();
        println("Fragment done!, starting!")
    }
    private fun DisplayInstructions(Score:String){
        val ft = getSupportFragmentManager().beginTransaction()
        val newFragment = PauseDialogFragment.newInstance(Score)
        newFragment.isCancelable = false
        newFragment.show(ft, "dialog")
    }


    /*
    * ##################################
    * Sprite animation methods start here
    * #############################
    * */
    fun CreatanimateSpriteImages(obje:AnimatedObj, res: Resources):AnimatedObj{
        //fun CreatanimateSpriteImages(rows:Int,columns:Int,width:Int,height:Int){
        val save = HashMap<Int,Bitmap>()
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bm: Bitmap?= BitmapFactory.decodeResource(res, obje.imageID, options)
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
        obje.setSave(save)
        return obje
    }

     fun startAnimateimg(obje:AnimatedObj, holder:ImageView){
        if (obje.frame == 0){
            obje.startAnim()
        }
        val nFrame = obje.getNextFrame()
        if(nFrame != null) {
            //animation_holder.setImageBitmap(nFrame)
            holder.setImageBitmap(nFrame)
        }
        if(!obje.isAnimDone() && obje.running){
            Handler().postDelayed(
                    Runnable {
                        if (obje.running) {
                            startAnimateimg(obje, holder)
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
            //this is so fucking ugly, but moving objects just won't work untill the screan HAVE BEEN loaded for some ms
            if(!fromOnCreat) {
                Handler().postDelayed(Runnable { setObjectPercentLocation("dragable_test", 45F, 80F) }, 200)
            }
            fromOnCreat = false
            //countController()
            // countDown()
            //timerCountDown()
            starAnim = CreatanimateSpriteImages(starAnim, getResources())
            Handler().postDelayed(Runnable { startAnimateimg(starAnim, animation_holder) }, 400)
        }
    }

    var timer:Job? = null;

    private fun countController(){
        if(timer == null) {
            timer = launch(UI) {
                while (timeLeft > 0 && IsStoped == false) {
                    timer_text.text = timeLeft.toString()
                    delay(1000)
                    println("timeLeft: " + timeLeft)
                    timeLeft -= 1
                }
                if (IsStoped == false) {
                    IsStoped = true
                    startSummary()
                }
            }
        }
    }
    private fun startSummary(){
        val intent = Intent(this, QuizSummary::class.java)
        intent.putExtra("Score", currentScore)
        intent.putExtra("QuizScore", QuizScore)
        intent.putExtra("killingSpree", longestkillingSpree)
        startActivity(intent)
    }

    private fun setTrashCaregory(){
        val cat = mutableListOf("GLASS","PAPER","ORGANIC","EWASTE","METAL","PLASTIC")
        cat.shuffle()
        for (i in 1..3){
            CanSetup[i] = cat[i]
        }
    }

    private fun setScreanSize(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screanWidth = displayMetrics.widthPixels
        screanHeight = displayMetrics.heightPixels
    }

    private fun limitAtEdges(){
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
    private fun getObjectPosInPercent(targetVievID: String): Pair<Float,Float>{

        val id: Int = resources.getIdentifier(targetVievID, "id", packageName)
        val target = findViewById<ImageView>(id)
        val prosX = ((target.x+ (target.width/2))/screanWidth!!)*100
        val prosY = ((target.y+ (target.height/2))/screanHeight!!)*100
        return Pair(prosX,prosY)
    }

    /*
        get distance betwen targetVievID and secoundTargetVievID in a normalized value based of percentage
     */
    private fun getDistanceinPercent(targetVievID: String,secoundTargetVievID: String):Double{
        val Pros_1 = getObjectPosInPercent(targetVievID)
        val Pros_2 = getObjectPosInPercent(secoundTargetVievID)
        val distPros = pow(pow((Pros_1.first - Pros_2.first).toDouble(),2.0) + pow((Pros_1.second - Pros_2.second).toDouble(),2.0),0.5)
        return distPros
    }

    /*
        get distance betwen targetVievID and secoundTargetVievID in pixels
     */
    fun getDistanceInPixels(targetVievID: String,secoundTargetVievID: String):Double{
        val id: Int = resources.getIdentifier(targetVievID, "id", packageName)
        val target = findViewById<ImageView>(id)
        val secoundTarget_id: Int = resources.getIdentifier(secoundTargetVievID, "id", packageName)
        val secound_target = findViewById<ImageView>(secoundTarget_id)
        val dist = pow(pow((target.x - secound_target.x).toDouble(),2.0) + pow((target.y - secound_target.y).toDouble(),2.0),0.5)
        return dist
    }

    fun setObjectPixelLocation(targetVievID: String,Xcord: Float,Ycord: Float){
        val id: Int = resources.getIdentifier(targetVievID, "id", packageName)
        val target = findViewById<ImageView>(id)
        target.x = Xcord
        target.y = Ycord
    }

    private fun setObjectPercentLocation(targetVievID: String, Xcord: Float, Ycord: Float){
        val id: Int = resources.getIdentifier(targetVievID, "id", packageName)
        val target = findViewById<ImageView>(id)
        target.x = (Xcord/100)*screanWidth!! - target.width/2
        target.y = (Ycord/100)*screanHeight!! - target.height/2
        println("Setting Xpos: "+target.x +"Setting Ypos: "+target.y)

    }
    fun setObjectPercentLocation(id: Int,Xcord: Float,Ycord: Float){

        val target = findViewById<ImageView>(id)
        target.x = (Xcord/100)*screanWidth!! - target.width/2
        target.y = (Ycord/100)*screanHeight!! - target.height/2
        println("Setting Xpos: "+target.x +"Setting Ypos: "+target.y)

    }

    private fun increaseScore(){
        currentScore ++
        score.text = "Score:" + (currentScore).toString()
    }


    private fun shakeIcon(targetVievID: String){
        val id: Int = resources.getIdentifier(targetVievID, "id", packageName)
        val target = findViewById<ImageView>(id)
        val rotate = ObjectAnimator.ofFloat(target, "rotation", 0f, 15f, 0f, -15f, 0f)
        // repeat the loop 6 times
        rotate.repeatCount = 6
        // animation play time 120 ms
        rotate.duration = 120
        rotate.start()
    }

    private fun setupTrashcanIcons(){
        val canIcons = hashMapOf<String,String>("GLASS" to "glassortering","PAPER" to "papperssortering",
                "ORGANIC" to "organisksortering","EWASTE" to "elektroniksortering",
                "METAL" to "metallsortering","PLASTIC" to "plastsortering")
        can_1.setImageResource(resources.getIdentifier(canIcons[CanSetup[1]], "drawable", packageName))
        can_2.setImageResource(resources.getIdentifier(canIcons[CanSetup[2]], "drawable", packageName))
        can_3.setImageResource(resources.getIdentifier(canIcons[CanSetup[3]], "drawable", packageName))
    }

    private fun changeObjectIcon(targetVievID: String,trashObj:Trash){
        val id: Int = resources.getIdentifier(targetVievID, "id", packageName)
        val target = findViewById<ImageView>(id)
        val ImgId = resources.getIdentifier(trashObj.trashIcon, "drawable", packageName)
        target.setImageResource(ImgId)
    }

    private fun addImage(){
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
    private fun getNewTrash():Trash{
        val tsh: MutableList<String> = mutableListOf(CanSetup[1]!!,CanSetup[2]!!,CanSetup[3]!!)
        val random = tsh.random()
        val newTrash = Trash(random!!,this)
        return newTrash
    }

    private fun updateStreak(status:Boolean){
        if(status){
            killingSpree++
        }else{
            killingSpree = 0
        }
        if (longestkillingSpree < killingSpree){
            longestkillingSpree = killingSpree
        }
        streak.text = "Streak:" + (killingSpree).toString()

    }

    private fun checkCollitionState(){
        limitAtEdges()
        var collided = false
        for (i in 1..3) {
            var distToCan = getDistanceinPercent("dragable_test","can_" + i.toString())
            if(distToCan < 12 && CanSetup[i] == activeTrash!!.trashTyp){
                //user released trash on the can
                increaseScore()
                shakeIcon("can_" + i.toString())

                var Tpos = getObjectPosInPercent("can_" + i.toString())
                setObjectPercentLocation("animation_holder",Tpos.first,Tpos.second)

                starAnim.resetFrame()
                startAnimateimg(starAnim, animation_holder)

                activeTrash = getNewTrash()//update the trash selected
                changeObjectIcon("dragable_test",activeTrash!!)
                setObjectPercentLocation("dragable_test",45F,80F)
                collided = true
                updateStreak(true)
                break
            }
        }
        if(!collided){
            setObjectPercentLocation("dragable_test",70F,70F)
            updateStreak(false)
        }
    }

    var ClickTime = 0L
    override fun onBackPressed() {
        //ClickTime = System.currentTimeMillis();
        var Tmp_Time = System.currentTimeMillis();
        if(Tmp_Time-ClickTime < 2000L && IsStoped == false){
            IsStoped = true

            try {
                timer!!.cancel()
            }catch (E:Exception){

            }
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "tryck bakåt igen för att avsluta", Toast.LENGTH_SHORT).show()
            ClickTime = System.currentTimeMillis();
        }
        //fragment are not displayed use standard back behavior
    }
}
