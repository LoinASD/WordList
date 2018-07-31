package io.cyanlab.wordlist.models.database

import java.util.ArrayList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.cyanlab.wordlist.models.pdf.Node

/**
 * Created by Анатолий on 13.03.2018.
 */
@Dao
interface NodeDao {

    @get:Query("SELECT * FROM node")
    val allNodes: List<Node>

    @Query("SELECT * FROM node WHERE nodeWLName = :name")
    fun getNodes(name: String): List<Node>

    @Query("SELECT * FROM node WHERE (nodeWLName = :name & id < :last)")
    fun getNodes(name: String, last: Int): List<Node>


    @Transaction
    @Insert
    fun insertAll(nodes: ArrayList<Node>)

    @Insert
    fun insertNode(node: Node): Long

    @Update
    fun updateNode(node: Node)

    @Transaction
    @Query("DELETE FROM node WHERE nodeWLName = :name")
    fun deleteNodes(name: String)

    @Delete
    fun deleteNode(node: Node)
}
