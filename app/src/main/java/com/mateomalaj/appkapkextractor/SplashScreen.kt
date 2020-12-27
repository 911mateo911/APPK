package com.mateomalaj.appkapkextractor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import layout.transitions.library.Fade
import layout.transitions.library.Transitions
import org.jetbrains.anko.find
import render.animations.Render
import render.animations.Slide

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val appkicon = find<ImageView>(R.id.splashimage)
        val maintext = find<TextView>(R.id.maintext)
        val secondtext = find<TextView>(R.id.secondarytext)

        val animations = Render(this)
        animations.setAnimation(Slide().InDown(appkicon))
        animations.setDuration(500)
        animations.start()
        animations.setAnimation(Slide().InUp(maintext))
        animations.setDuration(500)
        animations.start()
        animations.setAnimation(Slide().InUp(secondtext))
        animations.setDuration(500)
        animations.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,MainActivity()::class.java)
            startActivity(intent)
            val transitions = Transitions(this)
            transitions.setAnimation(Fade().InDown())
            finish()
        },600)
    }
}