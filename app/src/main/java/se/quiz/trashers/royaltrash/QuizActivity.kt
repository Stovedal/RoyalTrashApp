package se.quiz.trashers.royaltrash

import android.animation.ObjectAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import kotlinx.android.synthetic.main.activity_quiz.*
import android.view.View
import android.widget.Button
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class QuizActivity : AppCompatActivity() {
    var points = 0
    private val delayMillis = 1000L
    private val delayLongMillis = 5000L
    var questions:List<DBrequests.Question>? = null
    private var answers: Answers = Answers()
    lateinit var buttons:List<Button>


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
            //todo description for forts question
            delay(delayMillis)
            questions!!.forEach {
                if (currentRound != (questions!!.size - 1) && answers.latestCorrect) {
                    currentRound = answers.round
                    var description: DBrequests.Description? = null
                    val qDescription = launch {
                        try {
                            description = DBrequests().getDescription(it.q_id)
                        } catch (e:Exception) {
                            println(e.message)
                        }
                    }

                    val qCoroutine = launch(UI) {
                        question(it)
                    }
                    qCoroutine.join()
                    while (currentRound == answers.round) {}
                    qDescription.cancel()


                    if (description == null) {
                        delay(delayMillis)
                    } else {
                        launch(UI) {
                            question_text.text = description!!.description
                        }
                        val waitingThing = launch {
                            delay(delayLongMillis)
                        }
                        val quizLayout = findViewById<View>(R.id.quizLayout)

                        launch(UI) {
                            quizLayout.setOnClickListener {
                                waitingThing.cancel()
                            }
                        }
                        waitingThing.join()
                    }
                } else {
                    if(!blocker) {
                        blocker = true
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
                    points += 1
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
