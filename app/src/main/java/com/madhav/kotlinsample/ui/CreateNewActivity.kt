package com.madhav.kotlinsample.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.madhav.kotlinsample.Jdo.ToDoDetailJdo
import com.madhav.kotlinsample.R
import com.madhav.kotlinsample.database.ToDoTable
import com.madhav.kotlinsample.helper.Constants
import kotlinx.android.synthetic.main.activity_create_new.*
import java.util.*

/**
 * Created by madhav on 24/05/18.
 */
class CreateNewActivity : AppCompatActivity() {

    val TAG = "CreateNewActivity"
    var lItem = ""
    var IsEdit = false

    lateinit var saveBt : TextView
    lateinit var mStatusRadioGroup : RadioGroup

    lateinit var mFirebaseDatabase :  FirebaseDatabase
    lateinit var mDatabaseRef : DatabaseReference
    var lMutableList : MutableList<String>? = null

    var mToDoItem : ToDoDetailJdo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new)
        mStatusRadioGroup = findViewById(R.id.status_group)

        /*
        Firebase Database code
         */
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseRef = mFirebaseDatabase.getReference("message")
        mDatabaseRef = mFirebaseDatabase.getReference("First")

        mDatabaseRef.setValue("Hello")
        mDatabaseRef.setValue("How Are You?")


        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot?) {
                val value = datasnapshot!!.getValue()
                Log.d(TAG, "Value is: "+value)
            }

            override fun onCancelled(error: DatabaseError?) {
                Log.e(TAG, "Failed to Read Value: "+ error!!.toException())
            }
        })


        mToDoItem = intent.getParcelableExtra("todo_item")
        IsEdit    = intent.getBooleanExtra("isEdit", false)




        if(mToDoItem != null && IsEdit){
            todo_text.setText(mToDoItem!!.Item)
            todo_text.setSelection(mToDoItem!!.Item.length)
            save.visibility = View.GONE
            mStatusRadioGroup.visibility = View.VISIBLE

            when (mToDoItem!!.status) {
                "Pending" -> {
                    pending_rb.isChecked = true
                }
                "Complete" -> {
                    complete_rb.isChecked = true
                }
                "InProgress" -> {
                    wip_rb.isChecked = true
                }
            }
        }else{
            save.visibility = View.VISIBLE
            status_header.visibility = View.GONE
            mStatusRadioGroup.visibility = View.GONE
        }



        todo_text.setOnFocusChangeListener { view, b ->
            if(b){
                save.visibility = View.VISIBLE
            }
        }

        save.setOnClickListener({
             lItem = todo_text.text.toString().trim()
            if(lItem.length != 0 && !lItem.equals("")){
                saveToDoItem()
                if(IsEdit){
                    Toast.makeText(this@CreateNewActivity, "Successfully Updated!", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this@CreateNewActivity, "Successfully Saved!", Toast.LENGTH_LONG).show()
                }

                startActivity(Intent(this@CreateNewActivity, HomeActivity :: class.java))
                finish()
            }else {
                Toast.makeText(this@CreateNewActivity, "Enter some text", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun saveToDoItem(){
        if(IsEdit){

            var status = Constants().PENDING
            when (mStatusRadioGroup.checkedRadioButtonId) {
                R.id.pending_rb -> {
                    status = Constants().PENDING
                }
                R.id.wip_rb -> {
                    status = Constants().WORK_IN_PROGRESS
                }
                R.id.complete_rb -> {
                    status = Constants().COMPLETE
                }
            }

            val lJdo = ToDoDetailJdo(mToDoItem!!.key, lItem, Calendar.getInstance().timeInMillis.toString(), status)
            ToDoTable(this@CreateNewActivity).updateItem(lJdo)
        }else {

            val lJdo  = ToDoDetailJdo(UUID.randomUUID().toString(),lItem, Calendar.getInstance().timeInMillis.toString(), Constants().PENDING)
            ToDoTable(this@CreateNewActivity).inserAll(lJdo)
        }

    }






    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CreateNewActivity, HomeActivity :: class.java))
        finish()
    }
}