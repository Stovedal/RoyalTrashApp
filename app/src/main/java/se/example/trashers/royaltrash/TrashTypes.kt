package se.example.trashers.royaltrash

enum class TrashTypes(var TrashTypeIcons: List<String>) {

    Glass(listOf("glasobjekt1","glasobjekt1")),
    Organic(listOf("organisktobjekt1","organisktobjekt1")),
    Paper(listOf("pappersobjekt1","pappersobjekt1")),
    Ewaste(listOf("dummy","dummy")),
    Metal(listOf("dummy","dummy")),
    Plastic(listOf("dummy","dummy"))

}