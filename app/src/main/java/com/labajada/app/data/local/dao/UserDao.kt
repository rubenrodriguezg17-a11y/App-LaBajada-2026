package com.labajada.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid AND isActive = 1 LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("UPDATE users SET isBuyer = 1 WHERE uid = :uid")
    suspend fun activateBuyerRole(uid: String)

    @Query("UPDATE users SET fullName = :fullName, phoneNumber = :phoneNumber WHERE uid = :uid")
    suspend fun updateProfile(uid: String, fullName: String, phoneNumber: String)

    @Query("UPDATE users SET email = :email WHERE uid = :uid")
    suspend fun updateEmail(uid: String, email: String)

    @Query("UPDATE users SET isOwner = 1 WHERE uid = :uid")
    suspend fun activateOwnerRole(uid: String)

    // --- Sesión ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: SessionEntity)

    @Query("SELECT * FROM user_session WHERE id = 1 LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @Query("DELETE FROM user_session")
    suspend fun logout()

    @Query("UPDATE users SET isActive = 0 WHERE uid = :uid")
    suspend fun deactivateUser(uid: String)
}