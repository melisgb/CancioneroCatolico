package com.example.cancionerocatolico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.utils.UserHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Para declarar un subtitulo en el menu
        val actBar = supportActionBar
        actBar?.subtitle = UserHelper.getUserName(this)

        val viewSongsBtn = findViewById<Button>(R.id.btnViewSongs)
        viewSongsBtn.setOnClickListener {
            val intent = Intent(this, ViewSongsActivity::class.java)
            startActivity(intent)
        }

        val viewSongsByPartsBtn = findViewById<Button>(R.id.btnViewSongsByTags)
        viewSongsByPartsBtn.setOnClickListener {
            val intent = Intent(this, ViewSongsByKeyActivity::class.java)
            startActivity(intent)
        }

        val viewMyListsBtn = findViewById<Button>(R.id.btnViewMyLists)
        viewMyListsBtn.setOnClickListener {
            val intent = Intent(this, ViewMyListsActivity::class.java)
            startActivity(intent)
        }

    }
}