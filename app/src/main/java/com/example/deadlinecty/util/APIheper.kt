package com.example.deadlinecty.util

import android.util.Log
import com.example.deadlinecty.model.ConfigResponse
import com.example.deadlinecty.model.SessionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit
import com.example.deadlinecty.encodePasswordBase64
import com.example.deadlinecty.model.LoginBody
import com.example.deadlinecty2.data.SocketManager
import com.google.gson.Gson

fun getSession(context: android.content.Context, onComplete: () -> Unit) {
    val sharedPref = context.getSharedPreferences("myAppCache", android.content.Context.MODE_PRIVATE)
    RetrofitClient.instance.getSession(8003).enqueue(object : Callback<SessionModel> {
        override fun onResponse(call: Call<SessionModel>, response: Response<SessionModel>) {
            Log.d("API", "Session: $response")

            if (response.isSuccessful && response.body() != null) {
                val session = response.body()?.data ?: ""
                sharedPref.edit { putString("session", session) }
                Log.d("API", "Session: $session")
                onComplete()
            } else {
                Log.e("API", "Lỗi lấy session: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<SessionModel>, t: Throwable) {
            Log.e("API", "Thất bại lấy session: ${t.message}")
        }
    })
}

fun   getConfig(context: android.content.Context, restaurantName: String, onComplete: () -> Unit) {
    val sharedPref = context.getSharedPreferences("myAppCache", android.content.Context.MODE_PRIVATE)
    RetrofitClient.instance.getConfig(8003, "annhonquan", "net.techres.seemt.api").enqueue(object : Callback<ConfigResponse> {
        override fun onResponse(call: Call<ConfigResponse>, response: Response<ConfigResponse>) {
            if (response.isSuccessful && response.body()?.data != null) {
                val config = response.body()?.data
                sharedPref.edit()
                    .putString("api_key", config?.api_key ?: "")
                    .putString("type", config?.type ?: "")
                    .apply()
                Log.d("API", "Config OK: ${config?.api_key}")
                onComplete()
            } else {
                Log.e("API", "Lỗi lấy config: ${response.message()}")
            }
        }


        override fun onFailure(call: Call<ConfigResponse>, t: Throwable) {
            Log.e("API", "Thất bại lấy config: ${t.message}")
        }
    })
}

fun login(context: android.content.Context, username: String, password: String, onSuccess: () -> Unit) {
    val sharedPref = context.getSharedPreferences("myAppCache", android.content.Context.MODE_PRIVATE)
    val session = sharedPref.getString("session", "") ?: ""
    val apiKey = sharedPref.getString("api_key", "") ?: ""
    val type = sharedPref.getString("type", "") ?: ""

    val authorization = "$type $session:$apiKey"
    Log.d("token",authorization)
    var body = LoginBody(
        encodePasswordBase64(password),
        3,
        "7da73693c4e4b924",
        "",
        username,
        ""
    )

    Log.d("body", body.toString())
    RetrofitClient.instance.login(authorization, 8003, body)
        .enqueue(object : Callback<com.example.deadlinecty.model.LoginResponse> {
            override fun onResponse(call: Call<com.example.deadlinecty.model.LoginResponse>, response: Response<com.example.deadlinecty.model.LoginResponse>) {
                if (response.isSuccessful) {
                    val loginData = response.body()!!.data
                    Log.d("login", Gson().toJson(response.body()!!.data))
                    sharedPref.edit()
                        .putString("access_token", loginData?.access_token)
                        .putString("jwt_token", loginData?.jwt_token)
                        .putInt("user_id", loginData?.id ?: -1)
                        .commit()
                    Log.d("API", "Đăng nhập thành công: ${loginData?.username}")
                    val socketUrl = "https://realtime-chat.techres.vn"
                    SocketManager.connectSocket(context, socketUrl)
                    onSuccess()
                } else {
                    Log.e("API", "Đăng nhập thất bại1: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<com.example.deadlinecty.model.LoginResponse>, t: Throwable) {
                Log.e("API", "Gọi API thất bại2: ${t.message}")
            }
        })
}


