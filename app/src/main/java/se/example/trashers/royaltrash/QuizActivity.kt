package se.example.trashers.royaltrash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_quiz.*
import android.view.View
import android.widget.Button

class QuizActivity : AppCompatActivity() {
    var points = 0
    private val delayMillis = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        var questionNumber: Int = intent.getIntExtra("questionNumber", 0)

        //Throw the user back to the main menu if they didn't provide a questionNumber
        if (questionNumber == 0) {
            val i = Intent(this, MenuActivity::class.java)
            startActivity(i)
        }

        questionBasedQuiz(questionNumber)
    }

    private fun questionBasedQuiz(questionNumber: Int): Int {
        var questions = getQuestions(questionNumber).shuffled()

        question(0, questions)


        return points
    }

    private fun getQuestions(questionNumber: Int):List<Question> {
        val questions = mutableListOf<Question>()
        for (i in 0..(questionNumber-1)) {
            val q = Question(4)
            //todo check question IDs so they don't match any already in the list

            questions.add(i, q)
        }

        return questions
    }

    private fun question(round:Int, questions:List<Question>) {
        val buttons = listOf<Button>(quiz_button1, quiz_button2, quiz_button3, quiz_button4).shuffled()
        if (questions.size > round) {
            val question = questions[round]

            question_text.text = question.question

            buttons[0].text = question.answer
            buttons[1].text = question.falseAnswers[0]
            buttons[2].text = question.falseAnswers[1]
            buttons[3].text = question.falseAnswers[2]

            buttons.forEach {
                buttonColor(it, "default")
                it.setOnClickListener {
                    buttons.forEach {buttonColor(it, "disabled")}
                    if (buttons[0] == it) {
                        buttonColor(it, "true")
                        points += 1
                    } else {
                        buttonColor(it, "false")
                    }
                    removeListeners(buttons)

                    Handler().postDelayed({
                        question(round + 1, questions)
                    }, delayMillis)
                }
            }
        } else {
            endgame()
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
            "disabled" -> {button.setBackgroundResource(R.drawable.quiz_button_disabled)
            button.setEnabled(false)}
            else -> {button.setBackgroundResource(R.drawable.button)
                button.setEnabled(true)}
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
