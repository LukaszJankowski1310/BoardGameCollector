package com.example.boardgamecollector.recycleviews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boardgamecollector.R
import com.example.boardgamecollector.db.Game
import java.util.concurrent.Executors

class GamesRecycleViewAdapter(private val clickListener: (Game, View) -> Int) : RecyclerView.Adapter<GamesViewHolder>() {

    private val listOfItems = ArrayList<Game>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.game_list_item,parent,false)
        return GamesViewHolder(listItem)

    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        Log.i("pos", position.toString())
        holder.bind( listOfItems[position], position+1, clickListener)
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }


    fun setListOfItems(listItem : List<Game>) {
        listOfItems.clear()
        listOfItems.addAll(listItem)
    }

}

class GamesViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
    fun bind(game: Game, pos: Int, clickListener: (Game, View) -> Int){

//
        val orderNumberTextView = view.findViewById<TextView>(R.id.tvOrderNumber)
        val imageImageView = view.findViewById<ImageView>(R.id.ivImage)
        val titleAndYearTextView = view.findViewById<TextView>(R.id.tvTitle)
        val rankingTextView = view.findViewById<TextView>(R.id.tvRank)
//
        orderNumberTextView.text = pos.toString()
        titleAndYearTextView.text = "${game.title}\n(${game.year})"
        rankingTextView.text = "Ranking:\n${game.curr_position}"


        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        executor.execute {

            // Image URL
            val imageURL = game.image
            try {
                val ini = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(ini)
                handler.post {
                    imageImageView.setImageBitmap(image)
                }
            }

            catch (e: Exception) {
                e.printStackTrace()
            }
        }

        view.setOnClickListener {
            val id = clickListener(game, it)
            Log.i("id_game", id.toString() )
        }

    }
}