package com.a4gumel.taska.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(

    @PrimaryKey(autoGenerate=true)
    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val color: Int=-1,
): java.io.Serializable
