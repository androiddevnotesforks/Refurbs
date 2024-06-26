/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.contentprovidersample.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Data access object for Cheese.
 */
@Dao
interface CheeseDao {
    /**
     * Counts the number of cheeses in the table.
     *
     * @return The number of cheeses.
     */
    @Query("SELECT COUNT(*) FROM " + Cheese.TABLE_NAME)
    fun count(): Int

    /**
     * Inserts a [cheese] into the table.
     *
     * @param cheese A new [Cheese].
     * @return The row ID of the newly inserted cheese.
     */
    @Insert
    fun insert(cheese: Cheese): Long

    /**
     * Inserts multiple cheeses into the database
     *
     * @param cheeses An array of new cheeses.
     * @return The row IDs of the newly inserted cheeses.
     */
    @Insert
    fun insertAll(cheeses: Array<Cheese>): LongArray

    /**
     * Select all cheeses.
     *
     * @return A [Cursor] of all the cheeses in the table.
     */
    @Query("SELECT * FROM " + Cheese.TABLE_NAME)
    fun selectAll(): Cursor

    /**
     * Select a cheese by the ID.
     *
     * @param id The row ID.
     * @return A [Cursor] of the selected cheese.
     */
    @Query("SELECT * FROM " + Cheese.TABLE_NAME + " WHERE " + Cheese.COLUMN_ID + " = :id")
    fun selectById(id: Long): Cursor

    /**
     * Delete a cheese by the ID.
     *
     * @param id The row ID.
     * @return A number of cheeses deleted. This should always be `1`.
     */
    @Query("DELETE FROM " + Cheese.TABLE_NAME + " WHERE " + Cheese.COLUMN_ID + " = :id")
    fun deleteById(id: Long): Int

    /**
     * Update the cheese. The cheese is identified by the row ID.
     *
     * @param cheese The cheese to update.
     * @return A number of cheeses updated. This should always be `1`.
     */
    @Update
    fun update(cheese: Cheese): Int
}