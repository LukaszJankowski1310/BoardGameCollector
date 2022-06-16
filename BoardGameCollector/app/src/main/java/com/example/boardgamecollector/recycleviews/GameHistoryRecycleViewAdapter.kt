package com.example.boardgamecollector.recycleviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.boardgamecollector.R
import com.example.boardgamecollector.db.Game
import com.example.boardgamecollector.db.GameHistory

class GameHistoryRecycleViewAdapter() : RecyclerView.Adapter<GameHistoryViewHolder>() {

    private val listOfItems = ArrayList<GameHistory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.history_list_item,parent,false)
        return GameHistoryViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: GameHistoryViewHolder, position: Int) {
        holder.bind( listOfItems[position], position+1)
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    fun setListOfItems(listItem: List<GameHistory>) {
        listOfItems.clear()
        listOfItems.addAll(listItem)
    }
}


class GameHistoryViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {

    fun bind(gameHistory : GameHistory, pos : Int) {
        // setup items

        val historyOrderNumberTextView = view.findViewById<TextView>(R.id.tvHistoryOrderNumber)
        val historyDateTextView = view.findViewById<TextView>(R.id.tvHistoryDate)
        val historyRankTextView = view.findViewById<TextView>(R.id.tvHistoryRank)

        historyOrderNumberTextView.text = pos.toString()
        historyDateTextView.text = gameHistory.sync_date
        historyRankTextView.text = gameHistory.position.toString()




    }

}