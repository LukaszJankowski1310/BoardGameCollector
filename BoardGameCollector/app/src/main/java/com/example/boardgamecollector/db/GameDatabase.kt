package com.example.boardgamecollector.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Game::class, GameHistory::class],
    version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract val gameDao: GameDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "user_database2"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}