package se.example.trashers.royaltrash

import android.content.Context
import java.util.*

data class Trash(val trashTyp:String, val contex:Context) {

    var TrashIcon: String? = getIconNow(contex)
    var Categories:MutableList<String> = mutableListOf("GLASS","PAPER","ORGANIC","EWASTE","METAL","PLASTIC")
    val foundIcons = hashMapOf<String,MutableList<String>>()

    fun getIconName(typ:String): String? {
        when(typ){
            "GLASS" -> return TrashTypes.Glass.TrashTypeIcons
            "PAPER" -> return TrashTypes.Paper.TrashTypeIcons
            "ORGANIC" -> return TrashTypes.Organic.TrashTypeIcons
            "EWASTE" -> return TrashTypes.Ewaste.TrashTypeIcons
            "METAL" -> return TrashTypes.Metal.TrashTypeIcons
            "PLASTIC" -> return TrashTypes.Plastic.TrashTypeIcons
            else -> {
                println("Warning!, trashType " + typ +" not found!")
                return null
            }
        }
    }

    fun getIconNow(contex:Context):String?{
        var FoundList:MutableList<String> = mutableListOf()
        for (i in 1..99) {
            val ID = contex.getResources().getIdentifier(getIconName(trashTyp) + i.toString(), "drawable", contex.getPackageName())
            if (ID == 0) {
                break
            } else {
                FoundList.add(getIconName(trashTyp) + i.toString())
            }
        }
        val random = FoundList!!.random()
        return random
    }


    fun <L> List<L>.random(): L? = if (size > 0) get(Random().nextInt(size)) else null

}