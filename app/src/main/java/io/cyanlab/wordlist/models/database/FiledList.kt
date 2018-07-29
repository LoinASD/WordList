package io.cyanlab.wordlist.models.database


import androidx.room.Embedded
import androidx.room.Relation
import io.cyanlab.wordlist.models.pdf.Node
import io.cyanlab.wordlist.models.pdf.WordList


class FilledList {

    @Embedded
    var wordList: WordList? = null

    @Relation(parentColumn = "wlName", entityColumn = "nodeWLName")
    var nodes: List<Node>? = null
}
