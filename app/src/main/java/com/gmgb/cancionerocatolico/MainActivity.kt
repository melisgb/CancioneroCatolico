package com.gmgb.cancionerocatolico

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gmgb.cancionerocatolico.R
import com.gmgb.cancionerocatolico.objects.UserInfo
import com.gmgb.cancionerocatolico.utils.UserHelper

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

        val viewMyListsBtn = findViewById<Button>(R.id.btnViewMyLists)
        viewMyListsBtn.setOnClickListener {
            val intent = Intent(this, ViewMyListsActivity::class.java)
            startActivity(intent)
        }

        val playChordsBtn = findViewById<Button>(R.id.btnPlayChords)
        playChordsBtn.setOnClickListener {
            val intent = Intent(this, PlayChordActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.logout_menu, menu)
        val logoutItem = menu?.findItem(R.id.act_logout)

        logoutItem!!.setOnMenuItemClickListener {
            val savedInfo =
                UserInfo(applicationContext)
            savedInfo.clearUserInfo()
            Toast.makeText(applicationContext, getString(R.string.toast_logout_successful), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        return true
    }
}