package com.example.boardgamecollector

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boardgamecollector.databinding.FragmentGameHistoryBinding
import com.example.boardgamecollector.recycleviews.GameHistoryRecycleViewAdapter


class GameHistoryFragment : Fragment() {

    private lateinit var binding: FragmentGameHistoryBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var adapter : GameHistoryRecycleViewAdapter
    private lateinit var gameHistoryRecyclerView: RecyclerView

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

        binding = FragmentGameHistoryBinding.inflate(inflater, container, false)

        gameHistoryRecyclerView = binding.rvGameHistoryList
        gameHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = GameHistoryRecycleViewAdapter()

        gameHistoryRecyclerView.adapter = adapter



        viewModel.titleClickedGame.observe(viewLifecycleOwner) {
            binding.tvGameTitleHeader.text = it
        }

        viewModel.gameHistory?.observe(viewLifecycleOwner) {
            adapter.setListOfItems(it)
            adapter.notifyDataSetChanged()
        }






        return binding.root
    }



}