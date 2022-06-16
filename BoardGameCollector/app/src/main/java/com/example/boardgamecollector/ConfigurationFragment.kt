package com.example.boardgamecollector

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.boardgamecollector.databinding.FragmentConfigurationBinding
import kotlinx.coroutines.*
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.io.*
import javax.xml.parsers.DocumentBuilderFactory

class ConfigurationFragment : Fragment() {

    private lateinit var binding: FragmentConfigurationBinding
    private lateinit var sp: SharedPreferences
    private lateinit var editior: SharedPreferences.Editor



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = this.requireActivity()
            .getSharedPreferences(MainActivity.CONFIG_SP, Context.MODE_PRIVATE)
        editior = sp.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigurationBinding.inflate(inflater, container, false)


        binding.btnSubmit.setOnClickListener {
                var name: String? = null
                val fileName: String = "user.xml"
                //val userExists : Boolean
                name = binding.etName.text.toString()


                if (name.replace("\\s+".toRegex(), " ").length > 1) {
                  CoroutineScope(Dispatchers.IO).launch {
                      val info: String = downloadFile(
                          "https://boardgamegeek.com//xmlapi2/user?name=",
                          name,
                          fileName
                      )

                      val filesDir = activity?.filesDir
                      val testDirectory = File("$filesDir/XML")
                      val file = File(testDirectory, fileName)

                      val userExists : Boolean = checkUserExists(file)
                      withContext(Dispatchers.Main) {
                          if(!userExists) {
                              Toast.makeText(activity, "Użytkownik nieistnieje", Toast.LENGTH_LONG).show()
                          }
                          else {
                              editior.apply {
                                  putString(MainActivity.USER_NAME, name)
                                  commit()
                              }
                              it.findNavController().navigate(R.id.action_configurationFragment_to_synchronizingFragment)
                          }

                      }

                  }

                } else {
                    Toast.makeText(activity, "Podaj imie gracza", Toast.LENGTH_LONG).show()
                }

            }
        return binding.root
    }

    fun downloadFile(url_Str: String, playerName: String, fileName: String): String {
        try {
            // delay(1000)
            val url = URL(url_Str + playerName)
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

                for (i in 0..100) {
               //     Log.i("tag12", data[i].toChar().toString())
                }

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

    fun checkUserExists(xmlFile : File) : Boolean {

        if (xmlFile.exists()) {

                val xmlDoc: Document =
                    DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(xmlFile)

            xmlDoc.documentElement.normalize()
            val items: NodeList = xmlDoc.getElementsByTagName("user")
            for (i in 0 until items.length) {



                val attributeValue = items.item(i).attributes.getNamedItem("id").nodeValue

                if (attributeValue != "") {

                    return true
                }

            }
        }
        return false
    }

}


