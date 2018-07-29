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
    @Query("SELECT wlName, id FROM WordList WHERE id = :id")
    fun getWordlist(id: Int): FilledList

    @Transaction
    @Query("SELECT * FROM WordList WHERE wlName = :wlName")
    fun getWordlist(wlName: String): WordList

    @Update
    fun updateList(list: WordList)

    @Insert
    fun insertList(list: WordList): Long

    @Delete
    fun deleteList(list: WordList)

    @Query("DELETE FROM wordlist WHERE wlName = :wlName")
    fun deleteList(wlName: String)

    @Query("SELECT wlName FROM wordlist")
    fun loadNames(): List<String>


}
