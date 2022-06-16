package com.example.boardgamecollector.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import retrofit2.http.DELETE

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGameHistory(gameHistory: GameHistory)


    @Query ("DELETE FROM game_table")
    suspend fun clearGames()

    @Query ("DELETE FROM game_history" )
    suspend fun clearHistory()

    @Query("SELECT COUNT(*) FROM game_table WHERE curr_position > 0")
    fun getNumGames() : LiveData<Int>

    @Query("SELECT COUNT(*) FROM game_table WHERE curr_position = 0")
    fun getNumAddOns() : LiveData<Int>

    @Query("SELECT * FROM game_table WHERE curr_position > 0 ORDER BY curr_position")
    fun getAllGames() : LiveData<List<Game>>


    @Query("SELECT * FROM game_table WHERE curr_position = 0")
    fun getAllAddOns() : LiveData<List<Game>>

    @Query("SELECT * FROM game_history WHERE id_game = :id_g ORDER BY sync_date")
    fun getGameHistory(id_g : Int) : LiveData<List<GameHistory>>

}

