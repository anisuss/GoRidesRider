package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.*

@Dao
interface UserDAO {

    @Insert
    fun insert(userDatabase: UserDatabase)

    @Update
    fun update(userDatabase: UserDatabase)

    @Delete
    fun delete(userDatabase: UserDatabase)

    @Query("delete from user_table")
    fun deletAllUserObejcts()

    @Query("Select id from user_table")
    fun getUserId(): Int


}