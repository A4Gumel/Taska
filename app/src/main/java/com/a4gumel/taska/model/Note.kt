package com.a4gumel.taska.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Note(

    @PrimaryKey(autoGenerate=true)
    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val color: Int=-1,
): Parcelable
