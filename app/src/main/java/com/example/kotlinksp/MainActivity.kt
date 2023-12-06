package com.example.kotlinksp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val intent = navigateToActivityA(abc = 70, xyz = "quang", testObjet = TestObjet(listOf("hehe")))
        /*val intent = Intent(this, MainActivity2::class.java).apply {
            putExtra("INT_PARAM", 5)
        }*/
        //val intent = navigateToMainActivity2(abc = 70, xyz = "quang", testObjet = TestObjet(listOf("hehe")))
        //startActivity(intent)
    }
}