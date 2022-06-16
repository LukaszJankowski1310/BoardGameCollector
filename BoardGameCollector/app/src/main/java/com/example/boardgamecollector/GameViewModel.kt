package com.example.boardgamecollector

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boardgamecollector.db.Game
import com.example.boardgamecollector.db.GameDao
import com.example.boardgamecollector.db.GameHistory
import kotlinx.coroutines.launch

class GameViewModel(private val dao: GameDao) : ViewModel() {

    val numOfGames  = dao.getNumGames()
    val numOfAddOns  = dao.getNumAddOns()
    var allGames = dao.getAllGames()
    val allAddOns = dao.getAllAddOns()
    var idGameHistory = MutableLiveData<Int>()
    var titleClickedGame = MutableLiveData<String>()


    init {
        idGameHistory.value = 0
        titleClickedGame.value = ""


    }

    var gameHistory = idGameHistory.value?.let { dao.getGameHistory(it) }


    fun setIdGameHistory(id : Int) {
        gameHistory = dao.getGameHistory(id)
    }

    fun setClickedTitle(title : String) {
        titleClickedGame.value = title
    }


    fun clearGames()=viewModelScope.launch {
        dao.clearGames()
    }

    fun clearHistory()=viewModelScope.launch {
        dao.clearHistory()
    }

    fun addGame(game: Game) = viewModelScope.launch {
        dao.addGame(game)
    }

    fun addGameHistory(gameHistory : GameHistory) = viewModelScope.launch {
        dao.addGameHistory(gameHistory)
    }




}