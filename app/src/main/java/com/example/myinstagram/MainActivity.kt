package com.example.myinstagram

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var usernameView : EditText
    lateinit var userPassword1View : EditText
    lateinit var userPassword2View : EditText
    lateinit var registerBtn : TextView
    lateinit var loginBtn : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인이 되어있는 경우라면 바로 PostListActivity로 보낸다.
        if((application as MasterApplication).checkIsLogin()) {
            finish() // 종료시키고 넘어간다.
            startActivity(Intent(this, InstagramPostListActivity::class.java))
        } else {
            setContentView(R.layout.activity_main)

            initView(this@MainActivity)
            setupListener(this@MainActivity)
        }
    }

    fun setupListener(activity: Activity) {

        registerBtn.setOnClickListener {
            register(this@MainActivity)
        }

        loginBtn.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, LoginActivity::class.java)
            )
            //val sp = activity.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
            //val token = sp.getString("login_sp","")
            //Log.d("hello","token : " + token)
        }
    }

    fun register(activity: Activity) { // 가입 절차
        val username = getUserName()
        val password1 = getUserPassword1()
        val password2 = getUserPassword2()

        (application as MasterApplication).service.register( // 미리 만든 service 가져오기

            username, password1, password2

        ).enqueue(object :
            Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(activity, "가입에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful) {

                    Toast.makeText(activity, "가입에 성공하였습니다.", Toast.LENGTH_LONG).show()

                    val user = response.body()
                    val token = user!!.token!!

                    saveUserToken(token, activity) // token 저장하는 메서드 호출

                    (application as MasterApplication).createRetrofit()
                    activity.startActivity(
                        Intent(activity, InstagramPostListActivity::class.java)
                    )

                }
            }
        })
    }

    fun saveUserToken(token : String, activity: Activity) {
        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_sp", token) // token 저장하기.
        editor.commit()
    }


    // xml의 id와 password 가져오기
    fun initView(activity: Activity) {
        usernameView = activity.findViewById(R.id.username_inputbox)
        userPassword1View = activity.findViewById(R.id.password1_inputbox)
        userPassword2View = activity.findViewById(R.id.password2_inputbox)
        registerBtn = activity.findViewById(R.id.register)
        loginBtn = activity.findViewById(R.id.login)
    }

    fun getUserName() : String {
        return usernameView.text.toString()
    }

    // password 입력 받기
    fun getUserPassword1() : String {
        return userPassword1View.text.toString()
    }

    fun getUserPassword2() : String {
        return userPassword2View.text.toString()
    }

}