package com.u3coding.remoteadb

import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.DataOutputStream
import java.io.IOException
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commands = arrayListOf(
            "/system/bin/sh",
            "setprop service.adb.tcp.port 5555",
            "stop adbd",
            "start adbd"
        )
        try {
            RunAsRoot(commands)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun RunAsRoot(cmds: ArrayList<String>) {
        val p = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(p.outputStream)
        for (tmpCmd in cmds) {
            os.writeBytes(
                """
                $tmpCmd
                
                """.trimIndent()
            )
        }
        os.writeBytes("exit\n")
        os.flush()
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        AlertDialog.Builder(this).setMessage("设置完成，请在同一局域网下使用以下命令连接设备\nadb connect ip $ip:5555").setPositiveButton(
            "确认"
        ) { p0, p1 -> finish() }.show()
    }

}