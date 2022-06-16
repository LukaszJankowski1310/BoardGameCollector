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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boardgamecollector.databinding.FragmentAddOnBinding
import com.example.boardgamecollector.db.Game
import com.example.boardgamecollector.recycleviews.AddOnRecycleViewAdapter


class AddOnFragment : Fragment() {

    private lateinit var binding: FragmentAddOnBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var adapter : AddOnRecycleViewAdapter
    private lateinit var addOnsRecyclerView: RecyclerView


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

        binding = FragmentAddOnBinding.inflate(inflater, container, false)
        addOnsRecyclerView = binding.rvAddOnsList
        addOnsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AddOnRecycleViewAdapter()
        addOnsRecyclerView.adapter = adapter


       viewModel.allAddOns.observe(viewLifecycleOwner) {
           adapter.setListOfItems(it)
           adapter.notifyDataSetChanged()

       }

        return binding.root
    }

}