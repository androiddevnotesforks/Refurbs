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
package com.example.android.observability.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.android.observability.UserDataSource

/**
 * Factory for ViewModels
 */
class ViewModelFactory
/**
 * Our constructor, we just save our parameter `UserDataSource dataSource` in our field
 * `UserDataSource mDataSource`.
 *
 * @param mDataSource `UserDataSource` managing our `User` database
 */(
    /**
     * The `UserDataSource` managing our `User` database, set by our constructor.
     */
    private val mDataSource: UserDataSource) : ViewModelProvider.Factory {
    /**
     * Factory method for `UserViewModel` called by the `ViewModelProvider` to create an
     * instance of `UserViewModel`. We check to make sure that our parameter `modelClass`
     * is a type we can cast to `UserViewModel` before constructing a new instance using our
     * our field `UserDataSource mDataSource` as the database for that `UserViewModel`
     * and returning it to the caller. If it is not a proper class we throw IllegalArgumentException
     *
     * @param modelClass `UserViewModel.class`
     * @param <T>        `UserViewModel`
     * @param extras an additional information for this creation request
     * @return new instance of `UserViewModel`
     */
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by above if statement.
            return UserViewModel(mDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}