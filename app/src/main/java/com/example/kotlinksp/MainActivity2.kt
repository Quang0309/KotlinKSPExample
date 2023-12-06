package com.example.kotlinksp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.annotation.GenerateActivityExtensions
import com.example.annotation.NavigationParameter

@GenerateActivityExtensions
class MainActivity2 : AppCompatActivity() {

    @NavigationParameter(name = "INT_PARAM")
    var abc: Int = 0

    @NavigationParameter(name = "STRING_PARAM")
    var xyz: String? = "haha"

    /*@NavigationParameter(name = "PARCELABLE_PARAM")
    var testObjet: TestObjet? = null*/

    @NavigationParameter(name = "INT_ARRAY")
    lateinit var intArrayList: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        bindArgs()
        //Log.e("QuangLog", "$abc $xyz $testObjet")
    }
}