package com.madhav.kotlinsample.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView

import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.madhav.kotlinsample.Jdo.ToDoDetailJdo
import com.madhav.kotlinsample.R
import com.madhav.kotlinsample.adapter.TodoListAdapter
import com.madhav.kotlinsample.database.ToDoTable
import com.madhav.kotlinsample.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by madhav on 23/05/18.
 */
class HomeActivity  : AppCompatActivity(), OnItemClickListener {

    var mPopMenu : PopupMenu? = null
    lateinit var  mRecycleView : RecyclerView
    lateinit var mEmptyText : TextView
    lateinit var mContext :  Context
    var mAdapter :  TodoListAdapter? = null
    var mItemList : ArrayList<ToDoDetailJdo> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mContext = this@HomeActivity
        mRecycleView = findViewById(R.id.recycler_view)
        mEmptyText   = findViewById(R.id.empty_text)

        mRecycleView.layoutManager = LinearLayoutManager(mContext) as RecyclerView.LayoutManager?

        mPopMenu = android.support.v7.widget.PopupMenu(this, menu_place)
        mPopMenu!!.menuInflater.inflate(R.menu.home_menu, mPopMenu!!.menu)

        mItemList = ToDoTable(mContext).getAll() as ArrayList<ToDoDetailJdo>
        setContentToView();


        create_new.setOnClickListener {

            val intent = Intent(this@HomeActivity, CreateNewActivity :: class.java)
            intent.putExtra("isEdit", false)
            startActivity(intent)
            finish()
        }

        mPopMenu!!.setOnMenuItemClickListener({
            item : MenuItem ->

            when(item.itemId){
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    mContext.deleteDatabase("kotlinsample_mobile_database")
                    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@HomeActivity, "Logout!", Toast.LENGTH_LONG).show()
                    finish()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        })

        three_dot_option.setOnClickListener {
                mPopMenu!!.show()
        }

        return
    }


    fun setAdapter(){

        if(mAdapter == null){
            mAdapter = TodoListAdapter(mItemList, mContext, this)
            mRecycleView.adapter = mAdapter
        }else {
            mAdapter?.notifyDataSetChanged()
        }
    }



    fun setContentToView(){
        if(mItemList.size == 0){
            mEmptyText.visibility = View.VISIBLE
        }else {
            mEmptyText.visibility = View.GONE
            setAdapter()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onLongClick(position: Int) {

        showAlertDialog{

        setTitle("Want to Delete")
            setMessage("Delete this Item?")
            positiveButton  ("Yes"){
                val isDelete = ToDoTable(mContext).deleteItem(mItemList.get(position).key)
                if (isDelete){
                    mItemList.removeAt(position)
                    mAdapter!!.notifyItemRemoved(position)
                }
            }

            negativeButton("No") {DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }
            }
        }

    }


    override fun onClickListener(position: Int) {

        val intent = Intent(this@HomeActivity, CreateNewActivity :: class.java)
        intent.putExtra("todo_item", mItemList.get(position))
        intent.putExtra("isEdit", true)
        startActivity(intent)
        finish()
    }



    fun showAlertDialog( dialogBuilder : AlertDialog.Builder.()->Unit){
        val builder =  AlertDialog.Builder(this)
        builder.dialogBuilder()
        val dialog = builder.create()

        dialog.show()
    }

    fun AlertDialog.Builder.positiveButton(text : String = "Okay", handleClick : (which : Int) -> Unit = {}){
        this.setPositiveButton(text, {dialogInterface, which -> handleClick(which) })
    }

    fun AlertDialog.Builder.negativeButton(text: String = "Cancel", handleClick: (which: Int) -> Unit = {}){
        this.setNegativeButton(text, {dialogInterface, which -> handleClick(which) })
    }









}