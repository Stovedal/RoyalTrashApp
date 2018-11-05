package se.quiz.trashers.royaltrash

import android.animation.ObjectAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_quiz.*
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_throwing_trash.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class QuizActivity : AppCompatActivity(),PauseDialogFragment.FragmentCommunication{
    private val delayMillis = 15000L
    var questions:List<DBrequests.Question>? = null
    private var answers: Answers = Answers()
    lateinit var buttons:List<Button>
    private var Eggs = 0;

    var LockedByFragment = false;
    var newFragment:PauseDialogFragment? = null;
    override fun fragmentCommunicationStart() {
        //nexttap...
        LockedByFragment = false;
        println("Fragment done!, starting!")
    }

    private fun DisplayInstructions(Score:String){
        var ft = getSupportFragmentManager().beginTransaction()
        if(newFragment != null){//prevent dead fragments..
            try {
                newFragment!!.dismissAllowingStateLoss()
            }catch (E:Exception){}
        }
        newFragment = PauseDialogFragment.newInstance(Score)
        newFragment!!.isCancelable = false
        newFragment!!.show(ft, "dialog")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val questionNumber: Int = intent.getIntExtra("questionNumber", 0)
        buttons = listOf(quiz_button1, quiz_button2, quiz_button3, quiz_button4)

        progressBar.max = questionNumber * 100
        progressBar.progress = 0
        val progressAnimator = ObjectAnimator.ofInt(progressBar,
                "progress", progressBar.progress, progressBar.progress + 100)
        progressAnimator.setDuration(1000)
        progressAnimator.start()

        var data = getSharedPreferences("Data", 0)
        Eggs = data!!.getInt("Eggs", -1)
        if(Eggs > 7){
            quizLayout.setBackgroundResource(R.drawable.bg_egg)
            quiz_button1.setBackgroundResource(R.drawable.button_egg)
            quiz_button2.setBackgroundResource(R.drawable.button_egg)
            quiz_button3.setBackgroundResource(R.drawable.button_egg)
            quiz_button4.setBackgroundResource(R.drawable.button_egg)
        }


        questionBasedQuiz(questionNumber)
    }

    var blocker = false
    private fun questionBasedQuiz(questionNumber: Int) {
        val firstQ = launch {
            //todo handle network fail
            val firstQuestion = DBrequests().getRandomQuestion()
            val currentRound = answers.round
            questionCycle(firstQuestion, currentRound)

        }
        val loadedQs = launch {
            //todo handle network fail
            questions = DBrequests().getQuestions(questionNumber - 1)
        }

        launch {
            var currentRound = answers.round
            loadedQs.join()
            firstQ.join()
            //todo description for first question
            questions!!.forEach {
                if ((currentRound == (questions!!.size - 1) && !blocker) || !answers.latestCorrect) {
                    blocker = true
                    endgame()
                }
                currentRound = answers.round
                questionCycle(it, currentRound)
            }
        }
    }

    /**
     * Shows a question and attempts to get a description for that question.
     * If there is a description it will be showed after the question has been answered.
     * There is also a function that waits for user input or a timeout so the user has time
     * to read the descripton.
     * @param question the question
     * @param round the integer describing the current round, the function will proceed to the next round when answers.round changes
     * @see Answers
     * @see DBrequests.Question
     */
    suspend fun questionCycle(question:DBrequests.Question, round:Int) {
        var description: DBrequests.Description? = null
        val qDescription = launch {
            try {
                description = DBrequests().getDescription(question.q_id)
            } catch (e:Exception) {
                println(e.message)
            }
        }
        launch(UI) {
            question(question)
        }
        while (round == answers.round) {}
        qDescription.cancel()

        if (description != null) {
            launch(UI) {
                question_text.text = description!!.description
            }
        }
        LockedByFragment = true;
        DisplayInstructions("klicka för att fortsätta");
        var msPassed = 0;
        while(LockedByFragment){
            delay(50)
            msPassed += 50;
            if(msPassed > delayMillis){
                LockedByFragment = false;
                if(newFragment != null){//prevent dead fragments..
                    try {
                        newFragment!!.dismissAllowingStateLoss()
                    }catch (E:Exception){}
                }
            }
            //this is a lock
        }
        //waitForTimeoutOrTap()
    }

    /**
     * Waits for the next button to be pressed or the timeout in milliseconds
     * @see delayMillis
     */
    suspend fun waitForTimeoutOrTap() {
        val waitingThing = launch {
            delay(delayMillis)
        }
        val nextButton = findViewById<View>(R.id.next_button)

        launch(UI) {
            nextButton.visibility = View.VISIBLE
            nextButton.setOnClickListener {
                waitingThing.cancel()
            }
        }
        waitingThing.join()
        launch(UI) {
            nextButton.visibility = View.GONE
        }
    }

    /**
     * @param question
     * @see DBrequests.Question
     */
    private fun question(question: DBrequests.Question) {
        val alternatives:MutableList<String> = mutableListOf(question.answer, question.alternative1)
        if (question.alternative2 != "") {
            alternatives.add(question.alternative2)
        }
        if (question.alternative3 != "") {
            alternatives.add(question.alternative3)
        }

        question_text.text = question.question

        val buttonAdresses = mutableListOf(0, 1, 2, 3).subList(0, alternatives.size).shuffled()

        buttons.forEach {
            buttonColor(it, "default")
            it.visibility = View.INVISIBLE
        }

        var index = 0
        alternatives.forEach{alternative ->
            if (alternative != "") {
                val button = buttons[buttonAdresses.get(index)]
                button.text = alternative
                button.visibility = View.VISIBLE
                index++
            }
        }

        buttons.forEach { button ->

            button.setOnClickListener { clickedButton ->

                buttons.forEach {
                    buttonColor(it, "disabled")}

                if (buttons[buttonAdresses.get(0)] == clickedButton) {
                    buttonColor(clickedButton, "true")
                    answers.addAnswer(0, true)
                    val progressAnimator = ObjectAnimator.ofInt(progressBar,
                            "progress", progressBar.progress, progressBar.progress + 100)
                    progressAnimator.setDuration(1000)
                    progressAnimator.start()
                } else {
                    buttonColor(clickedButton, "false")
                    buttonColor(buttons[buttonAdresses.get(0)], "true")
                    answers.addAnswer(1, false)
                }
                removeListeners(buttons)
            }
        }
    }

    private fun endgame() {
        val intent = Intent(this, ThrowingTrashActivity::class.java)
        intent.putExtra("score", answers.getAnswers().size)
        startActivity(intent)
    }

    private fun buttonColor(button: View, color:String) {
        when (color) {
            "true" -> {button.setBackgroundResource(R.drawable.quiz_button_true)}
            "false" -> {button.setBackgroundResource(R.drawable.quiz_button_false)}
            "disabled" -> {
                if(Eggs > 7){
                    button.setBackgroundResource(R.drawable.button_egg)
                }else{
                    button.setBackgroundResource(R.drawable.quiz_button_disabled)
                }
                button.setEnabled(false)
            }
            else -> {
                if(Eggs > 7){
                    button.setBackgroundResource(R.drawable.button_egg)
                }else{
                    button.setBackgroundResource(R.drawable.button)
                }
                button.setEnabled(true)
                button.visibility = View.VISIBLE

            }
        }
    }

    private fun removeListeners(views:List<View>) {
        views.forEach {
            it.setOnClickListener(null)
        }

    }
    var ClickTime = 0L
    override fun onBackPressed() {
        //ClickTime = System.currentTimeMillis();
        var Tmp_Time = System.currentTimeMillis();
        if(Tmp_Time-ClickTime < 2000L){
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "tryck bakåt igen för att avsluta", Toast.LENGTH_SHORT).show()
            ClickTime = System.currentTimeMillis();
        }
        //fragment are not displayed use standard back behavior
    }

    override fun onResume() {
        super.onResume()
        //todo restore progress and current question
    }
}

class Answers {
    private var answers = mutableListOf<Int>()
    var round = 0
    var latestCorrect = true

    init {
        answers.add(0)
    }

    fun addAnswer(answer:Int, correct:Boolean) {
        answers.add(round, answer)
        latestCorrect = correct
        round++
    }

    fun getAnswers(): List<Int> {
        return answers
    }
}
