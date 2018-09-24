package se.example.trashers.royaltrash

enum class TrashTypes(var TrashTypeIcons: List<String>) {

    Glass(listOf("glasobjekt1","glasobjekt2")),
    Organic(listOf("organisktobjekt1","organisktobjekt2")),
    Paper(listOf("pappersobjekt1","pappersobjekt2")),
    Ewaste(listOf("elektronikobjekt1","elektronikobjekt2")),
    Metal(listOf("metallobjekt1","metallobjekt2")),
    Plastic(listOf("plastobjekt1","plastobjekt2"))

}