package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView

class ViewSongsByKeyActivity : AppCompatActivity() {
    var selectedFilters : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs_by_key)
        
        val intentExtra = intent.extras
        val key = intentExtra!!.getString("key")

        if(key == "parts"){
            val horizontalViewSeasons = findViewById<HorizontalScrollView>(R.id.chipsForSeasons)
            horizontalViewSeasons.visibility = View.INVISIBLE
        }
        if(key == "seasons"){
            val horizontalViewParts = findViewById<HorizontalScrollView>(R.id.chipsForParts)
            horizontalViewParts.visibility = View.INVISIBLE
        }

    }

    fun chipClicked(view: View) {

    }
}