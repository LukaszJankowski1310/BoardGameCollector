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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.boardgamecollector.databinding.FragmentSynchronizingBinding
import com.example.boardgamecollector.db.Game
import com.example.boardgamecollector.db.GameHistory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import kotlinx.coroutines.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException
import java.net.MalformedURLException
import java.net.URL
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

@RequiresApi(Build.VERSION_CODES.O)
class SynchronizingFragment : Fragment() {
    private lateinit var binding: FragmentSynchronizingBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editior: SharedPreferences.Editor
    private lateinit var userName : String
    private val viewModel: GameViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = this.requireActivity().getSharedPreferences(MainActivity.CONFIG_SP, Context.MODE_PRIVATE)
        editior = sp.edit()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSynchronizingBinding.inflate(inflater, container, false)
        userName  = sp.getString(MainActivity.USER_NAME, null).toString()

        val firstSyncDone: Boolean = sp.getBoolean(MainActivity.FIRST_SYNC_DONE, false)
        val lastSyncDate : String? = sp.getString(MainActivity.LAST_SYNC_DATE, null)

        if (firstSyncDone) {
            binding.lastSyncDateHeader.text = "Data ostatniej synchronizacji"
            binding.tvLastSyncDate.text = "$lastSyncDate"

        }
        else {
            binding.lastSyncDateHeader.text = "Witaj $userName ! Dokonaj pierwszej synchronizacji."
            binding.tvLastSyncDate.text = ""
        }
        binding.btnSync.setOnClickListener { view ->
            // synchrnonizajca
            if (firstSyncDone) {
               //  dodać pop up czy usunąc dane i wczytać nowe
                activity?.let {
                    MaterialAlertDialogBuilder(it)
                        .setTitle("Synchronizacja danych")
                        .setMessage("Czy chcesz usnuąć gry, które nie są już na liście")
                        .setNeutralButton("Zamknij") { dialog, which ->
                            return@setNeutralButton
                        }
                        .setPositiveButton("Tak") { dialog, which ->
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.clearGames()
                                handleSyncButton()
                                withContext(Dispatchers.Main){
                                    view.findNavController().navigate(R.id.action_synchronizingFragment_to_mainFragment)
                                }
                            }

                        }
                        .setNegativeButton("Nie") { dialog, which ->
                            CoroutineScope(Dispatchers.IO).launch {
                                handleSyncButton()
                                withContext(Dispatchers.Main){
                                    view.findNavController().navigate(R.id.action_synchronizingFragment_to_mainFragment)
                                }
                            }
                        }
                        .show()
                }


            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    handleSyncButton()

                    withContext(Dispatchers.Main){
                        view.findNavController().navigate(R.id.action_synchronizingFragment_to_mainFragment)
                    }
                }

            }


        }

        return binding.root
    }



    private suspend fun handleSyncButton() {

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedCurrentDate = current.format(formatter)
        val firstSyncDone: Boolean = sp.getBoolean(MainActivity.FIRST_SYNC_DONE, false)


        editior.apply {
            putBoolean(MainActivity.FIRST_SYNC_DONE, true)
            putString(MainActivity.LAST_SYNC_DATE, formattedCurrentDate)
            commit()
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(activity, "Proszę czekać ", Toast.LENGTH_SHORT).show()
        }

        val fileName: String = "userGames.xml"
        downloadFile("https://boardgamegeek.com/xmlapi2/collection?username=", userName, fileName)


        val filesDir = activity?.filesDir
        val testDirectory = File("$filesDir/XML")
        val file = File(testDirectory, fileName)
        writeGamesToDatabase(file, formattedCurrentDate)
    }


    fun downloadFile(url_Str: String, playerName: String, fileName: String): String {
        try {
            // delay(1000)
            val url = URL("$url_Str$playerName&stats=1")
            val connection = url.openConnection()
            connection.connect()
            val lengthOfFile = connection.contentLength
            val isStream = url.openStream()
            val filesDir = activity?.filesDir
            val testDirectory = File("$filesDir/XML")
            if (!testDirectory.exists()) testDirectory.mkdir()
            val fos = FileOutputStream("$testDirectory/$fileName")
            val data = ByteArray(1024)
            var count = 0
            var total: Long = 0
            var progress = 0
            count = isStream.read(data)

            while (count != -1) {
                total += count.toLong()
                val progress_temp = total.toInt() * 100 / lengthOfFile
                if (progress_temp % 10 == 0 && progress != progress_temp) {
                    progress = progress_temp
                }

                fos.write(data, 0, count)

                count = isStream.read(data)
            }
            isStream.close()
            fos.close()

        } catch (e: MalformedURLException) {
            return "Zly URL"
        } catch (e: FileNotFoundException) {
            return "Brak pliku"
        } catch (e: IOException) {
            return "Wyjątek IO"
        }

        return "sucess"
    }

    fun writeGamesToDatabase(xmlFile : File, syncDate : String ) {

        if (xmlFile.exists()) {
            val xmlDoc: Document =
                DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(xmlFile)


            var title : String = ""
            var yearPublished : Int = 0
            var id : Int = 0
            var currPosition : Int = 0
            var image : String = ""

            xmlDoc.documentElement.normalize()
            val items: NodeList = xmlDoc.getElementsByTagName("item")
            val ranks: NodeList = xmlDoc.getElementsByTagName("rank")

            val currPositions : MutableList<Int> = mutableListOf()


            Log.i("rankslen", ranks.length.toString())



            for (i in 0 until ranks.length) {
                val ranksNode : Node = ranks.item(i)

                val type : String = ranksNode.attributes.getNamedItem("type").nodeValue
                val name : String = ranksNode.attributes.getNamedItem("name").nodeValue



                if (type == "subtype" && name == "boardgame") {
                    var value : String = ranksNode.attributes.getNamedItem("value").nodeValue


                    try {
                        val valueInt = value.toInt()
                        currPositions.add(valueInt)
                    } catch (e : NumberFormatException) {
                        currPositions.add(0)
                    }
                }
            }

            for (i in 0 until items.length) {

                    val itemNode : Node = items.item(i)
                    val elem = itemNode as Element
                    val children = elem.childNodes

                    id = itemNode.attributes.getNamedItem("objectid").nodeValue.toInt()
                    try {
                        currPosition = currPositions.get(i)
                    } catch (e : IndexOutOfBoundsException) {
                        currPosition = 0
                    }

                    for (j in 0 until children.length) {
                        val node = children.item(j)
                        if (node is Element) {
                            when (node.nodeName) {
                                "name" -> {
                                    title = node.textContent
                                }
                                "yearpublished" -> {
                                    yearPublished = node.textContent.toInt()
                                }
                                "thumbnail" -> {
                                    image = node.textContent
                                }

                            }

                        }

                    }

                Log.i("liczba gier", viewModel.numOfGames.value.toString())

                viewModel.addGame(Game(
                    id, title, yearPublished, currPosition, image)
                )
                if (currPosition > 0) {
                    viewModel.addGameHistory(
                        GameHistory(
                            0, id, currPosition, syncDate
                        )
                    )
                }
            }

        }
    }


}


