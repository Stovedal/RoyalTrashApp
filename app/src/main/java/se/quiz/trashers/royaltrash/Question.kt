package se.quiz.trashers.royaltrash

class Question() {

    var q = DBrequests().getRandomQuestion()

    var questionId:Int = q.q_id
    var question:String = q.question
    var answer:String = q.answer
    var falseAnswers: List<String> = listOf(q.alternative1, q.alternative2, q.alternative3)

}