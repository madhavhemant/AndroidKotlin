package com.madhav.kotlinsample.helper

import android.content.Context
import android.content.SharedPreferences
import java.util.*

/**
 * Created by madhav on 24/05/18.
 */
class PreferenceHelper {

    var mContext : Context? = null;

    public var  FACEBOOK_ACESS_TOKEN = "facebook_token"
    public  val  USER_EMAIL = "user_email"



    public fun getSharedPreference(context : Context) : SharedPreferences{
        return context.getSharedPreferences("com.madhav.kotlinsample", 0)
    }

}