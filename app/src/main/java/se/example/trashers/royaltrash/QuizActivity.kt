package se.example.trashers.royaltrash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_quiz.*
import android.view.View
import android.widget.Button
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancel
import kotlinx.coroutines.experimental.defer
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class QuizActivity : AppCompatActivity() {
    var points = 0
    private val delayMillis = 2000L
    var questions:List<DBrequests.Question>? = null
    private var answers: Answers = Answers()
    lateinit var buttons:List<Button>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        buttons = listOf(quiz_button1, quiz_button2, quiz_button3, quiz_button4)

        val questionNumber: Int = intent.getIntExtra("questionNumber", 0)


        questionBasedQuiz(questionNumber)
    }
    var blocker = false
    private fun questionBasedQuiz(questionNumber: Int) {
        launch {
            //todo handle network fail
            val firstQuestion = DBrequests().getRandomQuestion()
            launch(UI) {
                question(firstQuestion)
            }
        }
        val loadedQs = launch {
            //todo handle network fail
            questions = DBrequests().getQuestions(questionNumber - 1)
        }

        launch {
            var currentRound = answers.round
            loadedQs.join()
            while (currentRound == answers.round) {}
            delay(delayMillis)

            questions!!.forEach {
                if (currentRound != questions!!.size && answers.latestCorrect) {
                    currentRound = answers.round
                    val qCoroutine = launch(UI) {
                        question(it)
                    }
                    val description = ""
                    val qDescription = launch {

                    }
                    qCoroutine.join()
                    while (currentRound == answers.round) {}
                    if (description.isEmpty()) {

                    }
                    delay(delayMillis)
                } else {
                    if(blocker == false) {
                        blocker = true;
                        endgame()
                    }
                }
            }
        }
    }

    /**
     * @param question
     */
    private fun question(question: DBrequests.Question) {
        buttons = buttons.shuffled()

        question_text.text = question.question

        buttons[0].text = question.answer
        buttons[1].text = question.alternative1
        buttons[2].text = question.alternative2
        buttons[3].text = question.alternative3

        buttons.forEach { button ->

            buttonColor(button, "default")
            button.setOnClickListener { clickedButton ->

                buttons.forEach {
                    buttonColor(it, "disabled")}

                if (buttons[0] == clickedButton) {
                    buttonColor(clickedButton, "true")
                    points += 1
                    answers.addAnswer(0, true)
                } else {
                    buttonColor(clickedButton, "false")
                    buttonColor(buttons[0], "true")
                    answers.addAnswer(1, false)
                }
                removeListeners(buttons)
            }
        }
    }

    private fun endgame() {
        println("startar aktivitet!")
        val intent = Intent(this, ThrowingTrashActivity::class.java)
        intent.putExtra("score", points)
        startActivity(intent)
    }

    private fun buttonColor(button: View, color:String) {
        when (color) {
            "true" -> {button.setBackgroundResource(R.drawable.quiz_button_true)}
            "false" -> {button.setBackgroundResource(R.drawable.quiz_button_false)}
            "disabled" -> {
                button.setBackgroundResource(R.drawable.quiz_button_disabled)
                button.setEnabled(false)
            }
            else -> {button.setBackgroundResource(R.drawable.button)
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
