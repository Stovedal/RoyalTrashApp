package se.example.trashers.royaltrash

enum class TrashTypes(var TrashTypeIcons: List<String>) {

    Glass(listOf("glasobjekt1","glasobjekt1")),
    Organic(listOf("organisktobjekt1","organisktobjekt1")),
    Paper(listOf("pappersobjekt1","pappersobjekt1")),
    Ewaste(listOf("elektronikobjekt1","elektronikobjekt1")),
    Metal(listOf("metallobjekt1","metallobjekt1")),
    Plastic(listOf("plastobjekt1","plastobjekt1"))

}