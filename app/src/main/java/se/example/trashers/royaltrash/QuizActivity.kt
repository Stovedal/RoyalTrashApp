package se.example.trashers.royaltrash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {
    var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        var bundle :Bundle ?=intent.extras
        var questionNumber: Int = intent.getIntExtra("questionNumber", 3)

        questionBasedQuiz(3)
    }

    fun questionBasedQuiz(questionNumber: Int): Int {
        var points = 0

        getQuestions(questionNumber).shuffled()

        quiz_button1.setOnClickListener {
            it.setBackgroundResource(R.drawable.quiz_button_true)
        }
        quiz_button2.setOnClickListener {

        }
        quiz_button3.setOnClickListener {

        }
        quiz_button4.setOnClickListener {

        }


        return points
    }

    fun getQuestions(questionNumber: Int):List<Question> {
        val questions = mutableListOf<Question>()
        for (i in 0..(questionNumber-1)) {
            val q = Question(4)

            questions.add(i, q)
        }

        return questions
    }

    fun question(question: String, answer: String, falseAnswers: List<String>):Int {
        var points = 0



        return points
    }
}
