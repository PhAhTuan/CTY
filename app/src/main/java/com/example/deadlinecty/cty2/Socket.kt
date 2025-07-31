package com.example.deadlinecty2.data

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.PollingXHR
import io.socket.engineio.client.transports.WebSocket
import java.util.Collections

object SocketManager {
    var socket: Socket? = null
        private set

    fun connectSocket(context:Context, serverUrl: String) {
        val sharedPref = context.getSharedPreferences("myAppCache", Context.MODE_PRIVATE)

        val token = sharedPref.getString("access_token", "")
        Log.d("tokencache", token.toString())
        val options = IO.Options.builder()
            .setExtraHeaders(
                Collections.singletonMap(
                    "Authorization",
                    listOf(token.toString())
                )
            )
            .setQuery(String.format("device=%s", "jjjj"))
            .setReconnection(true)
            .setTransports(arrayOf(Polling.NAME, WebSocket.NAME, PollingXHR.NAME))
            .build()
        try {
            Log.d("SocketIO", "Đang thử kết nối tới: $serverUrl")
            socket = IO.socket(serverUrl, options)


            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", " Kết nối thành công tới $serverUrl")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) {
                Log.e("SocketIO", " Kết nối thất bại ")
            }
            socket?.connect()


        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SocketIO", "Lỗi khi khởi tạo socket: ${e.message}")
        }
    }
    fun disconnectSocket() {
        socket?.disconnect()
        socket?.off()
    }
}






