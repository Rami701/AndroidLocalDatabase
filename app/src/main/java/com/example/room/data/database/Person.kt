package com.example.room.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Person (
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    @ColumnInfo val name:String,
    @ColumnInfo val age:Int,
)