package com.famco.googlemapsop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.famco.googlemapsop.mapScreen.MapsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            Handler().postDelayed({
                val i = Intent(this, MapsActivity::class.java)
                startActivity(i)
            }, 2000)
        }
}