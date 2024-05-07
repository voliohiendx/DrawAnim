package com.volio.vn.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.volio.vn.data.database.entities.Bitcoin

@Dao
interface BitcoinDao {

    @Query("SELECT * FROM Bitcoin")
    fun getAll(): List<Bitcoin>


    @Insert
    fun insertAll(vararg users: Bitcoin)


    @Delete
    fun delete(user: Bitcoin)

}