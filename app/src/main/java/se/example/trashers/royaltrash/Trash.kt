package se.example.trashers.royaltrash

import java.util.*

data class Trash(val trashTyp:String) {

    var TrashIcon: String? = GetIcon()

    fun GetIcon(): String? {
        var XType:List<String>? = null
        when(trashTyp){
            "GLASS" -> XType = TrashTypes.Glass.TrashTypeIcons
            "PAPER" -> XType = TrashTypes.Paper.TrashTypeIcons
            "ORGANIC" -> XType = TrashTypes.Organic.TrashTypeIcons
            "EWASTE" -> XType = TrashTypes.Ewaste.TrashTypeIcons
            "METAL" -> XType = TrashTypes.Metal.TrashTypeIcons
            "PLASTIC" -> XType = TrashTypes.Plastic.TrashTypeIcons
            else -> {
                println("Warning!, trashType " + trashTyp +" not found!")
            }
        }
        val random = XType!!.random()
        return random
    }

    fun <L> List<L>.random(): L? = if (size > 0) get(Random().nextInt(size)) else null

}