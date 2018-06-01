package com.madhav.kotlinsample.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.madhav.kotlinsample.R
import com.madhav.kotlinsample.helper.Helper
import com.madhav.kotlinsample.helper.PreferenceHelper

import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {


    val TAG  = "LoginActivity"


    val GOOGLE_SIGN_IN_CODE = 11111
    private var mFirebaseAuth : FirebaseAuth? = null
    private var mCallbackManager : CallbackManager? = null
    private var mLoginType = ""
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private var mSharedPreferences : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.


//        mSharedPreferences = this@LoginActivity.getSharedPreferences(resources.getString(R.string.shared_preference), android.content.Context.MODE_PRIVATE)
        mSharedPreferences = PreferenceHelper().getSharedPreference(this@LoginActivity)
        mFirebaseAuth = FirebaseAuth.getInstance()

        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        signup_bt.setOnClickListener({
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            finish()
        })

        facebook_login.setOnClickListener({
            mLoginType = "facebook"
            showProgress(true)
            facebookLogin()
        })

        google_login.setOnClickListener({
            mLoginType = "google"
            showProgress(true)
            googleLogin()
        })


        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    private fun facebookLogin() {

        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "Authentication Failed", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException?) {
                error!!.printStackTrace()
                Toast.makeText(this@LoginActivity, "Authentication Failed", Toast.LENGTH_LONG).show()
            }

        })
    }


    private fun googleLogin(){
        val gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(R.string.server_client_id.toString())
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        startActivityForResult(mGoogleSignInClient.signInIntent, GOOGLE_SIGN_IN_CODE)

    }

    fun handleFacebookAccessToken(accessToken : AccessToken){
        val credentail : AuthCredential = FacebookAuthProvider.getCredential(accessToken.token.toString())
        mFirebaseAuth!!.signInWithCredential(credentail)
                .addOnCompleteListener{task :  Task<AuthResult> ->
                    val lSharedPre = this.getPreferences(android.content.Context.MODE_PRIVATE)
                    with(lSharedPre.edit()){
                        putString(PreferenceHelper().FACEBOOK_ACESS_TOKEN, accessToken.token).commit()
                    }
                    saveFirebaseAuthUser(task)
                }
    }

    fun firebaseAuthWithGoogle(lAccount : GoogleSignInAccount){
        val lAuthCredential : AuthCredential = GoogleAuthProvider.getCredential(lAccount.idToken, null)
        mFirebaseAuth!!.signInWithCredential(lAuthCredential)
                .addOnCompleteListener{task : Task<AuthResult> ->
                    saveFirebaseAuthUser(task)
                }
    }

    /*
    email and Password Login
     */
    fun UserLogin(mEmail: String, mPassword: String){
        mLoginType = "email"
        try {

            mFirebaseAuth!!.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener{ task : Task<AuthResult> ->
                        Log.d(TAG, "Taks is: "+task.isSuccessful);
                        val lSharedPre = this.getPreferences(android.content.Context.MODE_PRIVATE)
                        with(lSharedPre.edit()){
                            putString(PreferenceHelper().USER_EMAIL, mEmail).commit()
                        }
                        saveFirebaseAuthUser(task)
                    }

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e.printStackTrace()
        }catch (e : FirebaseAuthInvalidUserException){
            e.printStackTrace()
        }
    }


    fun saveFirebaseAuthUser(task : Task<AuthResult>){
        if(task.isSuccessful){
            val lUser : FirebaseUser = mFirebaseAuth?.currentUser!!
            mFirebaseAuth!!.updateCurrentUser(lUser)
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_LONG).show()
            finish()
        }else {
            showProgress(false)
            Toast.makeText(this@LoginActivity, "Authentication Failed", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(mLoginType){
            "facebook"-> {
                mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
            }
            "google"->{
                if(requestCode == GOOGLE_SIGN_IN_CODE){
                    try{
                        val lTask : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                        val lAccount : GoogleSignInAccount = lTask.getResult(ApiException :: class.java)
                        firebaseAuthWithGoogle(lAccount)
                    }catch (e: ApiException){
                        e.printStackTrace()
                        showProgress(false)
                        Toast.makeText(this@LoginActivity, "Authentication Failed", Toast.LENGTH_LONG).show()
                    }


                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !Helper().isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!Helper().isValidEmail(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {

            focusView?.requestFocus()
        } else {
            showProgress(true)
//            mAuthTask = UserLoginTask(emailStr, passwordStr)
//            mAuthTask!!.execute(null as Void?)
            UserLogin(emailStr, passwordStr)
        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        email.setAdapter(adapter)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }




    companion object {
        private val REQUEST_READ_CONTACTS = 0
    }
}
