package com.a4gumel.taska.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.a4gumel.taska.model.Note


@Dao
interface DAO {

    @Insert
    suspend fun insertItem(note: Note)

    @Update
    suspend fun updateItem(note: Note)

    @Query("SELECT * FROM Note ORDER BY id DESC")
    fun getAllItems(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE :query OR content LIKE :query OR date LIKE:query ORDER BY id DESC")
    fun searchItem(query: String): LiveData<List<Note>>

    @Delete
    suspend fun deleteItem(note: Note)
}