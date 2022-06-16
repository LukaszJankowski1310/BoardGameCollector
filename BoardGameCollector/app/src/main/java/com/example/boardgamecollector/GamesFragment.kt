package com.example.boardgamecollector

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boardgamecollector.databinding.FragmentGamesBinding
import com.example.boardgamecollector.db.Game
import com.example.boardgamecollector.recycleviews.AddOnRecycleViewAdapter
import com.example.boardgamecollector.recycleviews.GamesRecycleViewAdapter


class GamesFragment : Fragment() {


    private lateinit var binding: FragmentGamesBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var adapter : GamesRecycleViewAdapter
    private lateinit var gamesRecyclerView: RecyclerView

    private val viewModel: GameViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sp = this.requireActivity()
            .getSharedPreferences(MainActivity.CONFIG_SP, Context.MODE_PRIVATE)
        editor = sp.edit()

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGamesBinding.inflate(inflater, container, false)


        gamesRecyclerView = binding.rvGamesList

        gamesRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = GamesRecycleViewAdapter {
                selectedGame : Game, view : View -> gameClickListener(selectedGame, view)
        }

        gamesRecyclerView.adapter = adapter

        viewModel.allGames.observe(viewLifecycleOwner) {
            adapter.setListOfItems(it)
            adapter.notifyDataSetChanged()

        }


        return binding.root
    }


    private fun gameClickListener(game : Game, view : View) : Int {

        Toast.makeText(activity, "Gra ${game.title}", Toast.LENGTH_SHORT).show()
        viewModel.setIdGameHistory(game.id)
        viewModel.setClickedTitle(game.title)

        view.findNavController()
            .navigate(R.id.action_gamesFragment_to_gameHistoryFragment)
        return game.id

    }

}