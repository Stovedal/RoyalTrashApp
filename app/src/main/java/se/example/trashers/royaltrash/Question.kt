package se.example.trashers.royaltrash

class Question() {

    var q = DBrequests().apiGetRandomQuestion()

    var questionId:Int = 0
    var question:String = ""
    var answer:String = ""
    var falseAnswers: List<String> = emptyList()

    constructor(numberOfAnswers:Int) : this() {
        questionId = getId()
        question = getQuestion(questionId)
        answer = getAnswer(questionId)
        falseAnswers = getFalseAnswers(questionId, numberOfAnswers)
    }


    fun getId():Int {
        //todo get an actual id from the database
        return q!!.q_id
    }

    fun getQuestion(questionId: Int): String {
        //todo get an actual question from the database
        return q!!.question
    }

    fun getAnswer(questionId: Int): String {
        //todo get a real answer
        return q!!.answer1
    }

    fun getFalseAnswers(questionId: Int, amount: Int): List<String> {
        //todo get false answers from database
        return listOf(q!!.answer2, q!!.answer3, q!!.answer4)
    }

}