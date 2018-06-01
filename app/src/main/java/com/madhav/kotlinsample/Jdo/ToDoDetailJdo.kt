package com.madhav.kotlinsample.Jdo

import android.os.Parcel
import android.os.Parcelable
import com.madhav.kotlinsample.helper.Constants
import java.util.*

/**
 * Created by madhav on 24/05/18.
 */
data class  ToDoDetailJdo(var key: String, var Item: String = "", var createdDate: String, var status: String = Constants().PENDING) : Parcelable {
    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel!!.writeString(key)
        parcel.writeString(Item)
        parcel.writeString(createdDate)
        parcel.writeString(status)
    }


    companion object CREATOR : Parcelable.Creator<ToDoDetailJdo> {
        override fun createFromParcel(parcel: Parcel): ToDoDetailJdo {
            return ToDoDetailJdo(parcel)
        }

        override fun newArray(size: Int): Array<ToDoDetailJdo?> {
            return arrayOfNulls(size)
        }
    }

}