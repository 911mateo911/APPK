package com.mateomalaj.appkapkextractor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import layout.transitions.library.Slide
import layout.transitions.library.Transitions
import org.jetbrains.anko.find

class Helpactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helpactivity)
        val backbtn = find<ImageView>(R.id.backbtn)
        backbtn.setOnClickListener {
            val transition = Transitions(this)
            super.onBackPressed()
            transition.setAnimation(Slide().InRight())
        }
    }
}