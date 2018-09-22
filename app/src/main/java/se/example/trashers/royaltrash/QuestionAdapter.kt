package se.example.trashers.royaltrash

interface QuestionAdapter {

    fun getId():Int {
        //todo get an actual id from the database
        return 1
    }

    fun getQuestion(questionId: Int): String {
        //todo get an actual question from the database
        return "Varför är himlen blå?"
    }

    fun getAnswer(questionId: Int): String {
        //todo get a real answer
        return "Därför"
    }

    fun getFalseAnswers(questionId: Int, amount: Int): List<String> {
        //todo get false answers from database
        return listOf("Den är röd egentligen", "Den är gjord av vatten", "Det är fint")
    }
}