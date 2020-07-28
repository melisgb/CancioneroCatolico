package com.example.cancionerocatolico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.UserInfo

class RegisterActivity : AppCompatActivity() {
    var cancAPI = CancioneroAPI({ -1 })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBtn = findViewById<Button>(R.id.btnRegister)
        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    fun registerUser(){
        val emailEText = findViewById<EditText>(R.id.etEmailRegister).text.toString()
        val usernameEText = findViewById<EditText>(R.id.etUsernameRegister).text.toString()
        val passwordEText = findViewById<EditText>(R.id.etPasswordRegister).text.toString()
        cancAPI.addUser(usernameEText, emailEText, passwordEText,
            success = {userID ->
                val savedInfo = UserInfo(applicationContext)
                savedInfo.saveUserInfo(userID, usernameEText)
                Toast.makeText(applicationContext, "Register successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
    }

}