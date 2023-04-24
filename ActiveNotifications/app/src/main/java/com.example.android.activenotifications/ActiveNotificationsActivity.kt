/*
 * Copyright 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("ReplaceNotNullAssertionWithElvisReturn")

package com.example.android.activenotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle

/**
 * TODO: Add kdoc
 */
class ActiveNotificationsActivity : MainActivity() {
    /**
     * A handle to our `ActiveNotificationsFragment` fragment
     */
    private var mFragment: ActiveNotificationsFragment? = null

    /**
     * `BroadcastReceiver` for the delete `Intent` sent when a notification is cleared.
     */
    private val mDeleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        /**
         * This method is called when the BroadcastReceiver is receiving an `Intent` broadcast.
         * If our field `ActiveNotificationsFragment mFragment` is null we call our `findFragment`
         * method to initialize it by finding the fragment with id R.id.sample_content_fragment first.
         * We then call the `updateNumberOfNotifications` method of `mFragment` to have
         * it request the current number of notifications from the `NotificationManager` and
         * display that number to the user.
         *
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        override fun onReceive(context: Context, intent: Intent) {
            if (mFragment == null) {
                findFragment()
            }
            mFragment!!.updateNumberOfNotifications()
        }
    }

    /**
     * Same as [.onCreate] but called for those activities created with
     * the attribute [android.R.attr.persistableMode] set to `persistAcrossReboots`.
     * First we call our super's implementation of `onCreate`, then we call our `findFragment`
     * method to initialize our field `ActiveNotificationsFragment mFragment` by finding the
     * fragment with id R.id.sample_content_fragment and then we call the `updateNumberOfNotifications`
     * of `mFragment` to have it request the current number of notifications from the `NotificationManager`
     * and display that number to the user. (Do not know if this is used or not?)
     *
     * @param savedInstanceState if the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     * ***Note: Otherwise it is null.*** We do not use
     * @param persistentState if the activity is being re-initialized after
     * previously being shut down or powered off then this Bundle contains the data it most
     * recently supplied to outPersistentState in [.onSaveInstanceState].
     * ***Note: Otherwise it is null.*** We do not use
     */
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        findFragment()
        mFragment!!.updateNumberOfNotifications()
    }

    /**
     * Convenience method to initialize our field `ActiveNotificationsFragment mFragment` by
     * finding the fragment with id R.id.sample_content_fragment.
     */
    private fun findFragment() {
        mFragment = supportFragmentManager
            .findFragmentById(R.id.sample_content_fragment) as ActiveNotificationsFragment?
    }

    /**
     * Dispatch onResume() to fragments. Note that for better inter-operation with older versions of
     * the platform, at the point of this call the fragments attached to the activity are *not*
     * resumed. First we call our super's implementation then we call the `registerReceiver`
     * method to register our field `BroadcastReceiver mDeleteReceiver` as a `BroadcastReceiver`
     * with a new instance of `IntentFilter` for the action ACTION_NOTIFICATION_DELETE as the
     * `Intent` broadcasts to be received.
     */
    override fun onResume() {
        super.onResume()
        registerReceiver(mDeleteReceiver, IntentFilter(ACTION_NOTIFICATION_DELETE))
    }

    /**
     * Dispatch onPause() to fragments. First we call our super's implementation of `onPause`,
     * then we call the `unregisterReceiver` method to unregister our `BroadcastReceiver mDeleteReceiver`
     * as a `BroadcastReceiver`.
     */
    override fun onPause() {
        super.onPause()
        unregisterReceiver(mDeleteReceiver)
    }

    companion object {
        /**
         * The action for the `IntentFilter` that our `BroadcastReceiver mDeleteReceiver`
         * listens for, it is set as the action of the delete `Intent` to send when the notification
         * added by `ActiveNotificationsFragment` is cleared explicitly by the user.
         */
        const val ACTION_NOTIFICATION_DELETE: String = "com.example.android.activenotifications.delete"
    }
}