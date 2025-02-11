package com.example.room.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Person::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
}