package com.example.boardgamecollector

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.boardgamecollector.databinding.FragmentConfigurationBinding
import com.example.boardgamecollector.databinding.FragmentMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess


class MainFragment : Fragment() {
    private lateinit var binding : FragmentMainBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editior: SharedPreferences.Editor
    private val viewModel: GameViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = this.requireActivity().getSharedPreferences(MainActivity.CONFIG_SP, Context.MODE_PRIVATE)
        editior = sp.edit()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)


        binding.apply {

            displayUserDetails()

            btnSync.setOnClickListener { view ->
                val lastSyncDate : String? =  sp.getString(MainActivity.LAST_SYNC_DATE, null)
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val formattedCurrentDate = current.format(formatter)

                if (lastSyncDate != null) {
                   if(checkDifferenceBetweenDates(lastSyncDate, formattedCurrentDate).toHours() < 24) {
                       var opt : Int = 1
                       Log.i("roznica", "roznica mniejsza niz 24 h wyswietl popup")
                       activity?.let {
                           MaterialAlertDialogBuilder(it)
                               .setTitle("Synchronizacja danych")
                               .setMessage("Od ostatniej synchronizacji minęło mniej niż 24 godziny. " +
                                       "Czy na pewno chcesz zsynchronizować aplikacje ?")
                               .setNeutralButton("Zamknij") { dialog, which ->
                                   return@setNeutralButton
                               }
                               .setPositiveButton("Tak") { dialog, which ->
                                   view.findNavController()
                                       .navigate(R.id.action_mainFragment_to_synchronizingFragment)
                               }
                               .setNegativeButton("Nie") { dialog, which ->
                                   return@setNegativeButton
                               }
                               .show()
                       }

                       return@setOnClickListener
                   }
                }
                view.findNavController()
                    .navigate(R.id.action_mainFragment_to_synchronizingFragment)
            }

            btnClear.setOnClickListener{view ->

                activity?.let {
                    MaterialAlertDialogBuilder(it)
                        .setTitle("Usuwanie danych")
                        .setMessage("Czy napewno chcesz usunąć dane ?")
                        .setNeutralButton("Zamknij") { dialog, which ->
                            Log.i("close", "popup zamknięty")

                        }
                        .setPositiveButton("Tak") { dialog, which ->
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.clearGames()
                                viewModel.clearHistory()
                                editior.clear().commit()

                               withContext(Dispatchers.Main) {
                                    activity?.finish();
                                    exitProcess(0);
                                }
                            }

                        }
                        .setNegativeButton("Nie") { dialog, which ->
                            Log.i("nie", "nie zgadzam sie")
                        }
                        .show()
                }
            }

            btnAddOn.setOnClickListener {view ->

                view.findNavController()
                    .navigate(R.id.action_mainFragment_to_addOnFragment)
            }

            btnGames.setOnClickListener { view ->
                view.findNavController()
                    .navigate(R.id.action_mainFragment_to_gamesFragment)
            }

        }

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDifferenceBetweenDates(last_sync : String, current_date : String) : Duration {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val lastSyncDate = LocalDateTime.parse(last_sync, formatter)
        val currentDate = LocalDateTime.parse(current_date, formatter)
        val diff : Duration = Duration.between(currentDate, lastSyncDate)
        return  diff
    }

    private fun displayUserDetails() {
        binding.apply {
            tvPlayerName.text = sp.getString(MainActivity.USER_NAME, null)
            tvLastSync.text = sp.getString(MainActivity.LAST_SYNC_DATE, null)

            viewModel.numOfGames.observe(viewLifecycleOwner, Observer {
                tvNumberGames.text = "Liczba gier:  ${it.toString()}"
            })

            viewModel.numOfAddOns.observe(viewLifecycleOwner, Observer {
                tvNumberAddOns.text = "Liczba dodatków:  ${it.toString()}"
            })
        }
    }





}


