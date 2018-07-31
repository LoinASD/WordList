package io.cyanlab.wordlist.models.database


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.cyanlab.wordlist.models.pdf.WordList

@Dao
interface ListDao {

    @get:Query("SELECT * FROM wordlist")
    val allLists: List<WordList>

    @Transaction
    @Query("SELECT * FROM WordList WHERE id = :id")
    fun getWordlist(id: Int): WordList

    @Transaction
    @Query("SELECT * FROM WordList WHERE name = :name")
    fun getWordlist(name: String): WordList

    @Update
    fun updateList(list: WordList)

    @Insert
    fun insertList(list: WordList): Long

    @Delete
    fun deleteList(list: WordList)

    @Query("DELETE FROM wordlist WHERE name = :name")
    fun deleteList(name: String)

    @Query("SELECT name FROM wordlist")
    fun loadNames(): List<String>


}
