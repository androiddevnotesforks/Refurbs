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
package com.example.android.observability

import com.example.android.observability.persistence.User
import io.reactivex.Flowable

/**
 * Access point for managing user data.
 */
interface UserDataSource {
    /**
     * Gets the [User] from the data source.
     *
     * @return the [User] from the data source.
     */
    fun getUser(): Flowable<User>

    /**
     * Inserts the [User] parameter [user] into the data source, or if this is an existing user,
     * updates it.
     *
     * @param user the [User] to be inserted or updated.
     */
    fun insertOrUpdateUser(user: User)

    /**
     * Deletes all users from the data source.
     */
    fun deleteAllUsers()
}
