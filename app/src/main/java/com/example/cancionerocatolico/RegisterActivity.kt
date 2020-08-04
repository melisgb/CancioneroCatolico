package com.example.cancionerocatolico

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.UserInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class RegisterActivity : AppCompatActivity() {
    var cancAPI = CancioneroAPI({ -1 })
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    var mGoogleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBtn = findViewById<Button>(R.id.btnRegister)
        registerBtn.setOnClickListener {
            registerUser()
        }

        //REF: https://developers.google.com/identity/sign-in/android/sign-in
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSigninBtn = findViewById<SignInButton>(R.id.btnGoogleSignin)
        googleSigninBtn.setSize(SignInButton.SIZE_STANDARD)
        googleSigninBtn.setOnClickListener {
            signin()
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
                finish()
            },
            fail ={
                Toast.makeText(applicationContext, "Registration failed", Toast.LENGTH_SHORT).show()
            })
    }

    fun signin(){
        val signinIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signinIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            //Signin task completed
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask : Task<GoogleSignInAccount>){
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Signin Activity", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(signedIn : GoogleSignInAccount?){
        if(signedIn != null){
            val username = signedIn.displayName.toString()
            Toast.makeText(applicationContext, "GoogleLogin successful ${signedIn.email.toString()}", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(applicationContext, "GoogleLogin failed", Toast.LENGTH_SHORT).show()
        }
    }

}