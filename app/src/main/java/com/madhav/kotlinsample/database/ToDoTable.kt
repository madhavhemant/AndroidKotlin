package com.madhav.kotlinsample.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.TouchDelegate
import com.madhav.kotlinsample.Jdo.ToDoDetailJdo
import java.security.Key

/**
 * Created by madhav on 24/05/18.
 */
class ToDoTable(pContext: Context)  {

     val TAG = "ToDoTable"


    var mDBHelper : ToDoDatabaseHelper? = null
    private val TABLE_NAME = "toDoTable"
    private val  KEY      = "key"
    private val TODO_ITEM = "todoItem"
    private val CREATED_DATE  = "createdDate"
    private val STATUS        = "status"


    private val CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY + " TEXT PRIMARY KEY," + TODO_ITEM + " TEXT, "+ CREATED_DATE + " TEXT, " + STATUS + " TEXT)"


        /*companion object {
            public val KEY : String = "_id"
            public val TODO_ITEM : String = "Todo_item"
            public val CREATED_DATE : String = "createdDate"
        }*/

    init {
        mDBHelper = ToDoDatabaseHelper(pContext)
    }


    public fun onCreate(pDb : SQLiteDatabase){
        pDb.execSQL(CREATE_TODO_TABLE)
        Log.d(TAG, "CREATE_TODO_TABLE: "+CREATE_TODO_TABLE)
    }


    /*
    insert To do Item
     */
    fun inserAll(pJDo : ToDoDetailJdo){

        val lDB : SQLiteDatabase = mDBHelper!!.writableDatabase
        try{
//            lDB.beginTransaction()
            val lContentValues :  ContentValues = generateContentValue(pJDo)
            val success = lDB.insert(TABLE_NAME,null, lContentValues)
            Log.d(TAG, "Inserted Value: "+success)
//            lDB.setTransactionSuccessful()
            lDB.close()

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
//            lDB.endTransaction()
            lDB.close()
        }
    }

    /*
    get all ToDo
     */
    fun getAll() : ArrayList<ToDoDetailJdo>?{
        val db : SQLiteDatabase = mDBHelper!!.readableDatabase
        val mList : ArrayList<ToDoDetailJdo>  = ArrayList()
        try{

            val query : String = "Select * from " + TABLE_NAME + " ORDER BY "+CREATED_DATE + " ASC"
            val cursor : Cursor = db.rawQuery(query, null)
            if(cursor.moveToFirst()){
                do{
                    val lToDoJdo  = ToDoDetailJdo(key = cursor.getString(0), Item = cursor.getString(1), createdDate = cursor.getString(2), status = cursor.getString(3))

                    lToDoJdo.key = cursor.getString(0)
                    lToDoJdo.Item = cursor.getString(1)
                    lToDoJdo.createdDate = cursor.getString(2)
                    mList.add(lToDoJdo)
                }while (cursor.moveToNext())
            }
            return mList
        }catch (e : Exception){
            e.printStackTrace()
            return null
        }finally {
            db.close()
        }
    }

    fun deleteItem(pKey : String) : Boolean{
        val db : SQLiteDatabase = mDBHelper!!.writableDatabase
        try{

            val lDeleteToDo = db.delete(TABLE_NAME, KEY + "=?", arrayOf(pKey) )
            Log.d(TAG, "Delete ToDo: "+lDeleteToDo)
            return true
        }catch (e : Exception){
            e.printStackTrace()
            return  false
        }finally {
            db.close()
        }
        return false
    }

    fun updateItem(pJdo : ToDoDetailJdo?) : Boolean{
        val db : SQLiteDatabase = mDBHelper!!.writableDatabase
        try{
            val lContentValues :  ContentValues = generateContentValue(pJdo!!)
            val lUpdateToDo = db.update(TABLE_NAME,lContentValues, KEY + "=?", arrayOf(pJdo.key))
            Log.d(TAG, "Update ToDo: "+lUpdateToDo)
            return true

        }catch (e : Exception){
            e.printStackTrace()
            return false
        }finally {
            db.close()
        }
    }

    fun generateContentValue(lJdo : ToDoDetailJdo) : ContentValues{
        val lValue = ContentValues()
        lValue.put(KEY, lJdo.key)
        lValue.put(TODO_ITEM, lJdo.Item)
        lValue.put(CREATED_DATE, lJdo.createdDate)
        lValue.put(STATUS, lJdo.status)
        return lValue
    }

}