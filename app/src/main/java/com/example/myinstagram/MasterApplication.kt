package com.example.myinstagram

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication : Application() {

    lateinit var service : RetrofitService

    // 액티비티보다 먼저 호출되는 onCreate이므로 여기서 설정(ex : Retrofit)하면 다른 액티비티에서 가져다 쓸 수 있다.
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this) // Stetho 초기화
        createRetrofit()

        //chrome://inspect/#devices

    }

    fun createRetrofit() {
        val header = Interceptor { // 원래 나가려던 통신을 잡아둔다. 개조하기 위해
            val original = it.request()

            if(checkIsLogin()) { // 로그인 했는지 안했는지 체크한다.
                getUserToken()?.let { token -> // null이 아니면 let 이하를 실행한다.
                    val request = original.newBuilder()
                        .header("Authorization", "token " + token) // 개조 : 헤더를 달아준다
                        .build()
                    it.proceed(request) // 다시 내보낸다.
                }
            } else {
                it.proceed(original) // 다시 내보낸다.
            }
        }

        val client = OkHttpClient.Builder() // 클라이언트 만들기
            .addInterceptor(header)  // 위에서 만든 헤더
            .addNetworkInterceptor(StethoInterceptor()) // 디바이스로부터 들어오고 나가는 어떤 네트워크 통신을 낚아채서 화면에 보여준다.
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // 위에서 만든 client를 등록(header까지 포함)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    // 로그인 했으면 헤더를 작성해준다.
    // 로그인 안했으면 헤더 작성할 필요가 없다.


    // 로그인 체크하는 메서드, sharedPreference에서 토큰 값이 있냐 없냐로 체크한다.
    fun checkIsLogin() : Boolean {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("login_sp", "null")
        if (token != "null") return true
        else return false
    }


    fun getUserToken() : String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("login_sp", "null")
        if(token == "null") return null
        else return token
    }



}