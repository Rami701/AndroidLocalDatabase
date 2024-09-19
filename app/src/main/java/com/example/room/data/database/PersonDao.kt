package com.example.room.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonDao {
    @Insert
    suspend fun createPerson(person: Person) : Long

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("Select * from person")
    suspend fun getAllPersons(): MutableList<Person>

    @Query("select * from person where name like '%' || :name || '%'")
    suspend fun getPersonByName(name:String) : MutableList<Person>

    @Update
    suspend fun updatePerson(person: Person)
}