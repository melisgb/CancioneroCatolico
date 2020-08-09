package com.example.cancionerocatolico

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.UserInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginActivity : AppCompatActivity() {
    var cancAPI = CancioneroAPI({ -1 })
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    var mGoogleSignInClient : GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = getString(R.string.login_title)

        val savedInfo = UserInfo(applicationContext)
        val userID = savedInfo.getUserID()
        if(userID != 0){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //REF: https://developers.google.com/identity/sign-in/android/sign-in
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSigninBtn = findViewById<Button>(R.id.btnLogin)
        googleSigninBtn.setOnClickListener {
            val signinIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signinIntent, 100)
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
            Log.e("Signin Activity", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(signedIn : GoogleSignInAccount?){
        if(signedIn != null){
            loginUser(signedIn.email!!, success = { result ->
                if(result) {
                    proceedToMain()
                }
                else {
                    registerUser(signedIn.displayName!!, signedIn.email!!)
                }
            })
        }
        else{
            Toast.makeText(applicationContext, getString(R.string.toast_login_failed), Toast.LENGTH_SHORT).show()
        }
    }
    private fun loginUser(email: String, success: (Boolean) -> Unit ) {
        cancAPI.loadUser(email,
            success = {user ->
                val savedInfo = UserInfo(applicationContext)
                savedInfo.saveUserInfo(user.userID, user.username)
                success(true)
            },
            fail = { errorMessage->
                if(errorMessage.equals("No user matches")){
                    success(false)
                }
                else{
                    Log.e("Server error", errorMessage)
                }
            })
    }
    private fun registerUser(username : String, email : String){
        cancAPI.addUser(username, email,
            success = { userID ->
                val savedInfo = UserInfo(applicationContext)
                savedInfo.saveUserInfo(userID, username)
                proceedToMain()
            },
            fail ={
            })
    }
    fun proceedToMain(){
        Toast.makeText(applicationContext, getString(R.string.toast_login_successful), Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}