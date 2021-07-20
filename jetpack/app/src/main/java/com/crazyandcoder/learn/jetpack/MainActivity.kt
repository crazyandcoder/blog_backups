package com.crazyandcoder.learn.jetpack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crazyandcoder.learn.jetpack.navigation.main.BottomNavigationActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //navigation
        navigationTv.setOnClickListener {
            startActivity(Intent(this@MainActivity, BottomNavigationActivity::class.java))
        }

    }
}