package com.madhav.kotlinsample.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by madhav on 25/05/18.
 */
class ToDoDatabaseHelper (context: Context) : SQLiteOpenHelper(context, "kotlinsample_mobile_database", null, 1) {

    var mContext : Context = context;

    override fun onCreate(db: SQLiteDatabase?) {
        try{
            ToDoTable(mContext).onCreate(db!!)
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}