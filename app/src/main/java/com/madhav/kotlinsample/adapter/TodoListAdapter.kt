package com.madhav.kotlinsample.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.madhav.kotlinsample.Jdo.ToDoDetailJdo
import com.madhav.kotlinsample.R
import com.madhav.kotlinsample.listener.OnItemClickListener
import kotlinx.android.synthetic.main.todo_item_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by madhav on 24/05/18.
 */
 class TodoListAdapter(val itemList : ArrayList<ToDoDetailJdo>?, val context : Context, val pItemClickListener: OnItemClickListener) : RecyclerView.Adapter<TodoListAdapter.ToDoViewHolder>() {

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bindItems(itemList!!.get(position))
        holder.lParentLayout.setOnLongClickListener { pItemClickListener.onLongClick(position)
        return@setOnLongClickListener true }
        holder.lParentLayout.setOnClickListener{pItemClickListener.onClickListener(position)}
    }



    class ToDoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

         var lParentLayout : RelativeLayout = itemView!!.findViewById(R.id.parent_layout)

        fun bindItems(item : ToDoDetailJdo) {
            itemView.item_text.text = item.Item
            itemView.create_date.text = SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH).format(Date(item.createdDate.toLong())).toString()


            when (item.status) {
                "Pending" -> {
                    itemView.status.setBackgroundResource(R.drawable.pending_bg)
                }
                "Complete" -> {
                    itemView.status.setBackgroundResource(R.drawable.complete_bg)
                }
                "InProgress" -> {
                    itemView.status.setBackgroundResource(R.drawable.wip_bg)
                }
            }
            itemView.status.text = item.status
        }
    }

    override fun getItemCount(): Int {
        return itemList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {

        return ToDoViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_item_row,parent, false))

    }

    companion object {
    }
}
