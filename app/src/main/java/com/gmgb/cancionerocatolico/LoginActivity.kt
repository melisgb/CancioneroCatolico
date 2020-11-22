package com.gmgb.cancionerocatolico

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gmgb.cancionerocatolico.R
import com.gmgb.cancionerocatolico.api.CancioneroAPI
import com.gmgb.cancionerocatolico.objects.UserInfo
import com.gmgb.cancionerocatolico.utils.UserHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginActivity : AppCompatActivity() {
    private var cancAPI = CancioneroAPI({ -1 })
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    private var mGoogleSignInClient : GoogleSignInClient? = null
    var isExistingUser : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = getString(R.string.login_title)

        val savedInfo =
            UserInfo(applicationContext)
        val userID = savedInfo.getUserID()
        if(userID != 0){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Generar el cliente de GoogleSign
        //REF: https://developers.google.com/identity/sign-in/android/sign-in
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSigninBtn = findViewById<Button>(R.id.btnLogin)
        googleSigninBtn.setOnClickListener {
            val signinIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signinIntent, 100)
            //llamara a onActivityResult
        }
    }

    override fun onRestart() {
        super.onRestart()
        val savedInfo =
            UserInfo(applicationContext)
        val userID = savedInfo.getUserID()
        if(userID != 0){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //resultado retornado del Google Signin Intent
        if(requestCode == 100){
            //Signin task completed
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask : Task<GoogleSignInAccount>){
        try {
            //El inicio de Google fue exitoso.
            val account =
                completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            Log.e("Signin Activity", "signInResult:failed code=" + e.statusCode, e )
            updateUI(null)
        }
    }

    private fun updateUI(signedIn : GoogleSignInAccount?){
        if(signedIn != null){
            Log.e("User SigninInfo", signedIn.email)
            loginUser(signedIn.email!!, success = { result ->
                if(result) {
                    isExistingUser = true
                    proceedToMain()
                }
                else {
                    isExistingUser = false
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
                val savedInfo = UserInfo(
                    applicationContext
                )
                savedInfo.saveUserInfo(user.userID, user.username)
                success(true)
            },
            fail = { errorMessage->
                if(errorMessage == "No user matches"){
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
                val savedInfo = UserInfo(
                    applicationContext
                )
                savedInfo.saveUserInfo(userID, username)
                proceedToMain()
            },
            fail ={
            })
    }
    private fun proceedToMain(){
        if(isExistingUser) Toast.makeText(applicationContext, getString(R.string.toast_login_welcome_back, UserHelper.getUserName(this)), Toast.LENGTH_SHORT).show()
        else Toast.makeText(applicationContext, getString(R.string.toast_login_welcome, UserHelper.getUserName(this)), Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}