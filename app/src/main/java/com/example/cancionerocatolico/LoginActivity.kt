package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.UserInfo

class LoginActivity : AppCompatActivity() {
    var cancAPI = CancioneroAPI({ -1 })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "Login"

        val savedInfo = UserInfo(applicationContext)
        val userID = savedInfo.getUserID()
        if(userID != 0){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val loginBtn = findViewById<Button>(R.id.btnLogin)
        loginBtn.setOnClickListener {
            loginUser()
        }
        val registerBtn = findViewById<Button>(R.id.btnGotoRegister)
        registerBtn.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onRestart() {
        super.onRestart()
        val savedInfo = UserInfo(applicationContext)
        val userID = savedInfo.getUserID()
        if(userID != 0){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun loginUser(){
        //TODO: see if Firebase will be needed or just the DB, so far it will be manually
        val emailEText = findViewById<EditText>(R.id.etEmailLogin)
        val passEText = findViewById<EditText>(R.id.etPasswordLogin)
        cancAPI.loadUser(emailEText.text.toString(), passEText.text.toString(),
            success = {user ->
                val savedInfo = UserInfo(applicationContext)
                savedInfo.saveUserInfo(user.userID, user.username)
                Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            fail = {
                Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT).show()
            })
    }
}