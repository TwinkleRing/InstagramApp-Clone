package com.example.myinstagram

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.myinstagram.databinding.ActivityInstagramMyPostListBinding
import com.example.myinstagram.databinding.ActivityInstagramUploadBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class InstagramUploadActivity : AppCompatActivity() {

    lateinit var filePath: String

    private lateinit var binding: ActivityInstagramUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstagramUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.viewPictures.setOnClickListener {
            getPicture()
        }
        binding.uploadPost.setOnClickListener {
            uploadPost()
        }


        binding.allList.setOnClickListener {
            startActivity(Intent(this, InstagramPostListActivity::class.java))
        }
        binding.myList.setOnClickListener {
            startActivity(Intent(this, InstagramMyPostListActivity::class.java))
        }
        binding.userInfo.setOnClickListener {
            startActivity(Intent(this, InstagramUserInfo::class.java))
        }
    }


    fun getPicture() { // 디바이스 앨범 접근하기
        val intent = Intent(Intent.ACTION_PICK)
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(intent, 1000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val uri : Uri = data!!.data!! // Uri = 어떤 자료의 위치
            filePath = getImageFilePath(uri)
            Log.d("hello","path : " + filePath)

        }
    }

    fun getImageFilePath(contentUri: Uri) : String { // 상대 경로를 넣어주면 파일의 절대 경로(파일의 full path) 얻는 메서드
        var columnIndex = 0
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        return cursor.getString(columnIndex)
    }

    fun uploadPost() {
        val file = File(filePath)
        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        val content = RequestBody.create(MediaType.parse("text/plain"), getContent())

        (application as MasterApplication).service.uploadPost(
            part, content
        ).enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {

            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(response.isSuccessful) {
                    finish()
                    startActivity(Intent(this@InstagramUploadActivity, InstagramMyPostListActivity::class.java))
                }
            }
        })
    }

    fun getContent() : String {
        return binding.contentInput.text.toString()
    }
}