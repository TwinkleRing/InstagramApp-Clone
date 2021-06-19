package com.example.myinstagram

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myinstagram.databinding.ActivityInstagramUserInfoBinding

class InstagramUserInfo : AppCompatActivity() {
    private lateinit var binding: ActivityInstagramUserInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstagramUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.allList.setOnClickListener {
            startActivity(Intent(this, InstagramPostListActivity::class.java))
        }
        binding.myList.setOnClickListener {
            startActivity(Intent(this, InstagramMyPostListActivity::class.java))
        }
        binding.upload.setOnClickListener {
            startActivity(Intent(this, InstagramUploadActivity::class.java))
        }

        binding.logout.setOnClickListener {
            val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("login_sp", "null")
            editor.commit()
            (application as MasterApplication).createRetrofit()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}