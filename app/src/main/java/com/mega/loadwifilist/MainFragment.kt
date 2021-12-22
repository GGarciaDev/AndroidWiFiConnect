package com.mega.loadwifilist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import java.io.StringReader


class MainFragment : Fragment() {

    interface URICallback {
        fun onUriAcquired(uri: Uri)
    }

    class WiFi(val ssid: String, val password: String, val security: String)
    private var wifiList: ArrayList<WiFi>? = null
    lateinit var observer : MainLifecycleObserver
    lateinit var wfMgr: WifiManager

    fun connectToWifi(ssid: String, key: String, security: String) {
        val intent = Intent(requireActivity(), JoinWifi::class.java)
        intent.putExtra("ssid",ssid)
        intent.putExtra("password_type",security)
        intent.putExtra("password",key)
        startActivity(intent)
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiList = arrayListOf<WiFi>()
        wfMgr = requireActivity().getSystemService(Context.WIFI_SERVICE) as WifiManager
        observer = MainLifecycleObserver(requireActivity().activityResultRegistry, object: URICallback{
            override fun onUriAcquired(uri: Uri) {
                Log.d("MainZ", "Got an file")
                val jsonSelectedFile = requireActivity().contentResolver.openInputStream(uri!!);
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
                Toast.makeText(requireContext(), "Finish importing ${wifiList!!.size} wifi configs" , Toast.LENGTH_LONG).show()

                Log.d("MainZ", "Got an file")
                Log.d("MainZ", "Start connecting to wifi")
                wifiList!!.forEach{
                    connectToWifi(it.ssid, it.password, it.security)
                }
            }
        })
        lifecycle.addObserver(observer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn_load = requireActivity().findViewById<Button>(R.id.btn_load)
        btn_load.setOnClickListener {
            Log.d("MainZ", "Button clicked")
            observer.selectFile()
        }
    }

}