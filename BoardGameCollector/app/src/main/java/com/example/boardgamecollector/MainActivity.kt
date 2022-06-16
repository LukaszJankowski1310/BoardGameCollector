package com.example.boardgamecollector


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.boardgamecollector.databinding.ActivityMainBinding
import com.example.boardgamecollector.db.GameDatabase

import kotlinx.coroutines.delay
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var sp:SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    private lateinit var viewModel : GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        sp = getSharedPreferences(CONFIG_SP, MODE_PRIVATE)
        editor = sp.edit()
        setupStartFragmentDestination()


        val dao = GameDatabase.getInstance(application).gameDao
        val factory = GameViewModelFactory(dao)
        viewModel = ViewModelProvider(
            this, factory ).get(GameViewModel::class.java)

    }



    private fun setupStartFragmentDestination() {


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        val firstSyncDone : Boolean = sp.getBoolean(FIRST_SYNC_DONE, false)

        if (!firstSyncDone) {
            navGraph.setStartDestination(R.id.configurationFragment)
        }
         else {
            navGraph.setStartDestination(R.id.mainFragment)
        }
        navController.graph = navGraph
    }
    companion object {
        const val CONFIG_SP : String = "config_sp"
        const val USER_NAME : String = "user_name"
        const val FIRST_SYNC_DONE : String = "first_sync_done"
        const val LAST_SYNC_DATE : String = "last_sync_date"
    }

}