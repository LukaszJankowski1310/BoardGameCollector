package com.example.boardgamecollector.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id : Int,
    @ColumnInfo(name = "id_game")
    val id_game : Int,
    @ColumnInfo(name = "position")
    val position : Int,
    @ColumnInfo(name = "sync_date")
    val sync_date : String
)
