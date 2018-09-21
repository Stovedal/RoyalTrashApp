package se.example.trashers.royaltrash

class Question(): QuestionAdapter {
    var questionId:Int = 0
    var question:String = ""
    var answer:String = ""
    var falseAnswers: List<String>? = null

    constructor(numberOfAnswers:Int) : this() {
        questionId = getId();
        question = getQuestion(questionId)
        answer = getAnswer(questionId)
        falseAnswers = getFalseAnswers(questionId, numberOfAnswers)
    }


}