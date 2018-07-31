package io.cyanlab.wordlist.models.database

import io.cyanlab.wordlist.models.pdf.Node
import io.cyanlab.wordlist.models.pdf.WordList

interface DBHolder{

    var database: WordlistsDatabase?

    fun saveNodes(name: String?, nodes: ArrayList<Node>){

        val list = WordList()

        list.name = name

        database?.listDao()?.insertList(list)

        database?.nodeDao()?.insertAll(nodes)
    }

}