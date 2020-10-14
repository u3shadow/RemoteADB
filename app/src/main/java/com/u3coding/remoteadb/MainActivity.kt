package com.u3coding.remoteadb

import android.content.Context
import android.content.DialogInterface
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
            AlertDialog.Builder(this).setMessage("该程序仅工作在拥有root权限设备上，如果没有root，请连接usb并使用下列命令进行远程调试\nadb tcpip 5555\nadb connect $ip:5555").setPositiveButton(
                "确认"
            ) { _, _ ->
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
            }.setCancelable(false).show()

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
        ) { _, _ -> finish() }.setCancelable(false).show()
    }

}