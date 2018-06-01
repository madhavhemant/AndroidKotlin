package com.madhav.kotlinsample.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.madhav.kotlinsample.R
import com.madhav.kotlinsample.helper.Helper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by madhav on 23/05/18.
 */
class SignupActivity : AppCompatActivity() {


    private var mFirebaseAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        mFirebaseAuth = FirebaseAuth.getInstance()

        phone_no.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                validation()
                return@OnEditorActionListener true
            }
            false
        })

        register_button.setOnClickListener({
            validation()
        })


        login_bt.setOnClickListener({
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    fun validation(){

        email_signup.error = null
        password_signup.error = null

        val lEmailStr  = email_signup.text.toString()
        val lPassword  = password_signup.text.toString()
        val lMobile    = phone_no.text.toString()

        var cancel: Boolean = false
        var focusView : View? = null

        if(TextUtils.isEmpty(lEmailStr) && !Helper().isPasswordValid(lPassword)){
            password_signup.error = getString(R.string.error_invalid_password)
            focusView = password_signup
            cancel = true
        }

        if(TextUtils.isEmpty(lEmailStr)){
            email_signup.error = getString(R.string.error_field_required)
            cancel = true
            focusView = email_signup
        }else if(!Helper().isValidEmail(lEmailStr)){
            email_signup.error = getString(R.string.error_invalid_email)
            cancel = true
            focusView = email_signup
        }

        if(cancel){
            focusView?.requestFocus()
        }else {
            showProgress(true)
            UsersignUp(lEmailStr, lPassword, lMobile)
        }


    }


    fun UsersignUp(pEmail: String, pPassword: String, pNumber : String){
        try {

            mFirebaseAuth!!.createUserWithEmailAndPassword(pEmail, pPassword)
                    .addOnCompleteListener({ pTask : Task<AuthResult> ->
                        if(pTask.isSuccessful){
                            val lUser : FirebaseUser = mFirebaseAuth?.currentUser!!
                            mFirebaseAuth!!.updateCurrentUser(lUser)
                            val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_LONG).show()
                            finish()
                        }else {
                            Toast.makeText(this@SignupActivity, "Authentication Failed", Toast.LENGTH_LONG).show()
                        }
                    })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            signup_form.visibility = if (show) View.GONE else View.VISIBLE
            signup_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            signup_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            signup_progress.visibility = if (show) View.VISIBLE else View.GONE
            signup_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            signup_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            signup_progress.visibility = if (show) View.VISIBLE else View.GONE
            signup_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


}