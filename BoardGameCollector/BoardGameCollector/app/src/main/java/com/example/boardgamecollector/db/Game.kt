package com.example.boardgamecollector.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "game_table")
data class Game(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id : Int,
    @ColumnInfo(name = "title")
    val title : String,
    @ColumnInfo(name = "year")
    val year : Int,
    @ColumnInfo(name = "curr_position")
    val curr_position : Int,
    @ColumnInfo(name = "image")
    val image : String
)
