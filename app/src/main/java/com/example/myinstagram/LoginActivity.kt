package com.example.myinstagram

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myinstagram.databinding.ActivityLoginBinding
import org.mozilla.javascript.tools.jsc.Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 로그인하기
        binding.login.setOnClickListener {
            val username = binding.usernameInputbox.text.toString()
            val password = binding.passwordInputbox.text.toString()


            // 통신 보내기
            (application as MasterApplication).service.login(
                username, password
            ).enqueue(object: Callback<User> {

                override fun onFailure(call: Call<User>, t: Throwable) {

                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        val token = user!!.token!!
                        saveUserToken(token, this@LoginActivity)
                        (application as MasterApplication).createRetrofit()

                        //Toast.makeText(this@LoginActivity, "로그인 하셨습니다.", Toast.LENGTH_LONG).show()
                        Log.d("hello","로그인 했당")


                        startActivity(
                            Intent(this@LoginActivity, InstagramPostListActivity::class.java)
                        )

                    }

                }
            })
        }
    }

    fun saveUserToken(token : String, activity : Activity) {
        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_sp", token)
        editor.commit()
    }

}