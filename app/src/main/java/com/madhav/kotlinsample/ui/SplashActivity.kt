package com.madhav.kotlinsample.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.madhav.kotlinsample.R

/**
 * Created by madhav on 24/05/18.
 */
class SplashActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mFirebaseAuth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Handler().postDelayed({
            val lCurrentUser : FirebaseUser? = mFirebaseAuth?.currentUser

            if(lCurrentUser != null){
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                startActivity(Intent(this@SplashActivity, LoginActivity :: class.java))
                finish()
            }
        }, 1000)



    }

    override fun onStart() {
        super.onStart()
    }
}