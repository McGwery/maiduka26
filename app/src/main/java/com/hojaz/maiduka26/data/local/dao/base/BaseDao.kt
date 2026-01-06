package com.hojaz.maiduka26.data.local.dao.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base DAO interface with common CRUD operations.
 * All DAOs should extend this interface to maintain consistency.
 */
interface BaseDao<T> {

    /**
     * Inserts a single entity into the database.
     * On conflict, replaces the existing entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    /**
     * Inserts multiple entities into the database.
     * On conflict, replaces existing entities.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Updates an existing entity in the database.
     */
    @Update
    suspend fun update(entity: T): Int

    /**
     * Updates multiple entities in the database.
     */
    @Update
    suspend fun updateAll(entities: List<T>): Int

    /**
     * Deletes an entity from the database.
     */
    @Delete
    suspend fun delete(entity: T): Int

    /**
     * Deletes multiple entities from the database.
     */
    @Delete
    suspend fun deleteAll(entities: List<T>): Int
}

