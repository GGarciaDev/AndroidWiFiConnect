package com.mega.loadwifilist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import java.io.StringReader


class MainActivity : AppCompatActivity() {
    class WiFi(val ssid: String, val password: String, val security: String)
    private var wifiList: ArrayList<WiFi>? = null

    fun connectToWifi(ssid: String, key: String, security: String) {
        Log.d("MainZ", "Connecting to ${ssid} pass ${key} over ${security}")
        val intent = Intent(this, JoinWifi::class.java)
        intent.putExtra("ssid",ssid)
        intent.putExtra("password_type",security)
        intent.putExtra("password",key)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wifiList = arrayListOf<WiFi>()
        val btn_load = findViewById<Button>(R.id.btn_load)
        btn_load.setOnClickListener {
            Log.d("MainZ", "Button clicked")
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d("MainZ", "Got an file")
            val jsonSelectedFile = contentResolver.openInputStream(selectedFile!!);
            val inputAsString = jsonSelectedFile!!.bufferedReader().use { it.readText() }
            val klaxon = Klaxon()
            JsonReader(StringReader(inputAsString)).use { reader ->
                reader.beginArray {
                    while (reader.hasNext()) {
                        val wifi = klaxon.parse<WiFi>(reader)
                        wifiList!!.add(wifi!!)
                    }
                }
            }
            Toast.makeText(this, "Finish importing ${wifiList!!.size} wifi configs" , Toast.LENGTH_LONG).show()

            Log.d("MainZ", "Got an file")
            Log.d("MainZ", "Start connecting to wifi")
            wifiList!!.forEach{
                connectToWifi(it.ssid, it.password, it.security)
            }
        }
    }
}