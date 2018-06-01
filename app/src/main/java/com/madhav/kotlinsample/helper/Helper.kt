package com.madhav.kotlinsample.helper

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by madhav on 23/05/18.
 */
class Helper {

    var VALID_EMAIL_ADDRESS_REGEX : Pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$", Pattern.CASE_INSENSITIVE)

    fun isValidEmail(pEmail : String) : Boolean{
        val inputStr : CharSequence = pEmail
        val matcher : Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(inputStr)
        return matcher.matches()
    }


    fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }
}