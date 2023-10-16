@file:Suppress("ReplaceNotNullAssertionWithElvisReturn", "MemberVisibilityCanBePrivate")

package com.example.android.activenotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.common.logger.Log

/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
 * [String] used to request the "POST_NOTIFICATIONS" permission.
 */
private const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

/**
 * A fragment that allows notifications to be enqueued.
 */
open class ActiveNotificationsFragment : Fragment() {
    /**
     * Handle to the system level service NOTIFICATION_SERVICE, which we use to post notifications.
     */
    private var mNM: NotificationManager? = null

    /**
     * `TextView` with the resource id R.id.number_of_notifications in which we display the
     * number of notifications we currently have displayed in the status bar.
     */
    private var mNumberOfNotifications: TextView? = null
    private var mDeletePendingIntent: PendingIntent? = null

    /**
     * Called to have the fragment instantiate its user interface view. We use our parameter
     * `LayoutInflater inflater` to inflate our layout file R.layout.fragment_notification_builder
     * using our parameter `ViewGroup container` for the layout params without attaching to it,
     * and return the `View` created to our caller.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached
     * to.  The fragment should not add the view itself, but this can be used to
     * generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here. We do not use.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification_builder, container, false)
    }

    /**
     * Called when the fragment is visible to the user and actively running. First we call our super's
     * implementation of `onResume`, then we call our method `updateNumberOfNotifications`
     * to retrieve the number of notifications this app has displayed in the status bar, and display
     * this number in our `TextView mNumberOfNotifications`.
     */
    override fun onResume() {
        super.onResume()
        updateNumberOfNotifications()
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view. First we call our
     * super's implementation of `onViewCreated`, and then we initialize our field
     * `NotificationManager mNM` with a handle to the system level service NOTIFICATION_SERVICE.
     * We initialize `NotificationChannel chan1` with a new instance whose ID is PRIMARY_CHANNEL,
     * ("default") whose user visible name is also PRIMARY_CHANNEL, and whose importance is IMPORTANCE_DEFAULT
     * (Default notification importance: shows everywhere, makes noise, but does not visually intrude).
     * We set the notification light color for notifications posted to `chan1` to be GREEN, its
     * lock-screen visibility to VISIBILITY_PRIVATE (show this notification on all lockscreens, but
     * conceal sensitive or private information on secure lockscreens), then call the `createNotificationChannel`
     * method of `mNM` to create `NotificationChannel chan1`. We then initialize our field
     * `TextView mNumberOfNotifications` by finding the view with id R.id.number_of_notifications.
     * We initialize our variable `View.OnClickListener onClickListener` with a new instance whose
     * `onClick` override calls our method `addNotificationAndUpdateSummaries` to post a
     * new notification, update our usage of notification grouping for multiple notifications, and
     * displays the number of notifications we have active in `TextView mNumberOfNotifications`
     * (it does all this only if the id of the view that was clicked is R.id.add_notification). We
     * then find the `View` in our parameter `View view` whose id is R.id.add_notification
     * and set its `OnClickListener` to `onClickListener`. We initialize `Intent deleteIntent`
     * with an instance whose action is ACTION_NOTIFICATION_DELETE, then initialize our field
     * `PendingIntent mDeletePendingIntent` with will perform a broadcast of `deleteIntent`
     * with a private request code of REQUEST_CODE, and 0 for the flags.
     *
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestNotificationPermission()
        mNM = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chan1 = NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL,
            NotificationManager.IMPORTANCE_DEFAULT)
        chan1.lightColor = Color.GREEN
        chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        mNM!!.createNotificationChannel(chan1)
        mNumberOfNotifications = view.findViewById(R.id.number_of_notifications)

        // Supply actions to the button that is displayed on screen.
        val onClickListener = View.OnClickListener { v ->
            if (v.id == R.id.add_notification) {
                addNotificationAndUpdateSummaries()
            }
        }
        view.findViewById<View>(R.id.add_notification).setOnClickListener(onClickListener)

        // Create a PendingIntent to be fired upon deletion of a Notification.
        val deleteIntent = Intent(ActiveNotificationsActivity.ACTION_NOTIFICATION_DELETE)
        mDeletePendingIntent = PendingIntent.getBroadcast(activity,
            REQUEST_CODE, deleteIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            actionRequestPermission.launch(arrayOf(POST_NOTIFICATIONS))
            return
        }
    }

    private val actionRequestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }

    /**
     * Adds a new [Notification] with sample data and sends it to the system. Then updates the
     * current number of displayed notifications for this application and creates a notification
     * summary if more than one notification exists. First we initialize `Notification.Builder builder`
     * with a new instance that will post to channel PRIMARY_CHANNEL, set its small icon to the icon
     * with resource id R.mipmap.ic_notification, set its title to the string with resource id R.string.app_name
     * ("ActiveNotifications"), set its content text to the string with resource id R.string.sample_notification_content
     * ("This is a sample notification"), set its auto cancel flag (notification automatically dismissed
     * when the user touches it), set the intent to execute when the notification is explicitly dismissed
     * by the user to `PendingIntent mDeletePendingIntent`, and set its group to NOTIFICATION_GROUP.
     * We initialize `Notification notification` by building `builder`, then use `mNM`
     * to post `notification` using the value returned by our method `getNewNotificationId`
     * as the unique ID for this notification. We log what we did, then call our method `updateNotificationSummary`
     * to add update or remove the notification summary as necessary, and call our method `updateNumberOfNotifications`
     * to retrieve the number of notifications we have active and display them in our field `TextView mNumberOfNotifications`.
     */
    private fun addNotificationAndUpdateSummaries() {
        // Create a Notification and notify the system.
        val builder = Notification.Builder(activity, PRIMARY_CHANNEL)
            .setSmallIcon(R.mipmap.ic_notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.sample_notification_content))
            .setAutoCancel(true)
            .setDeleteIntent(mDeletePendingIntent)
            .setGroup(NOTIFICATION_GROUP)
        val notification = builder.build()
        mNM!!.notify(newNotificationId, notification)
        // [END create_notification]
        Log.i(TAG, "Add a notification")
        updateNotificationSummary()
        updateNumberOfNotifications()
    }

    /**
     * Adds/updates/removes the notification summary as necessary. First we initialize our variable
     * `int numberOfNotifications` with the value that our `getNumberOfNotifications`
     * method returns. We then branch on the value of `numberOfNotifications`
     *
     *  *
     * Greater than 1: We initialize `String notificationContent` with the formatted
     * string formed from `numberOfNotifications` and the format string with resource
     * id R.string.sample_notification_summary_content ("There are %d ActiveNotifications").
     * We initialize `Notification.Builder builder` with a new instance whose notification
     * will be posted to channel PRIMARY_CHANNEL, set its small icon to the png with resource
     * id R.mipmap.ic_notification, set the rich notification style to be applied at build time
     * to a new instance of `Notification.BigTextStyle` (a Helper class for generating
     * large-format notifications that include a lot of text) whose first line of text after
     * the detail section in the big form of the template is the string `notificationContent`,
     * set the group of our builder to NOTIFICATION_GROUP, and set it to be the group summary
     * for a group of notifications. We then initialize `Notification notification` by
     * building `builder`, and use `mNM` to post `notification` using the
     * id NOTIFICATION_GROUP_SUMMARY_ID (it is our summary group).
     *
     *  *
     * Not greater than 1: We use `NotificationManager mNM` to cancel the notification
     * summary.
     *
     *
     */
    protected fun updateNotificationSummary() {
        val numberOfNotifications = numberOfNotifications
        if (numberOfNotifications > 1) {
            // Add/update the notification summary.
            val notificationContent = getString(
                R.string.sample_notification_summary_content,  // format "There are %d ActiveNotifications."
                numberOfNotifications // Number of notifications we have displayed
            )
            val builder = Notification.Builder(activity, PRIMARY_CHANNEL)
                .setSmallIcon(R.mipmap.ic_notification)
                .setStyle(Notification.BigTextStyle().setSummaryText(notificationContent))
                .setGroup(NOTIFICATION_GROUP)
                .setGroupSummary(true)
            val notification = builder.build()
            mNM!!.notify(NOTIFICATION_GROUP_SUMMARY_ID, notification)
        } else {
            // Remove the notification summary.
            mNM!!.cancel(NOTIFICATION_GROUP_SUMMARY_ID)
        }
    }

    /**
     * Requests the current number of notifications from the [NotificationManager] to initialize
     * our variable `int numberOfNotifications` by calling our method `getNumberOfNotifications`
     * and displays them to the user in `TextView mNumberOfNotifications` using the format string
     * with the resource id R.string.active_notifications ("Active Notifications: %1$d").
     */
    fun updateNumberOfNotifications() {
        val numberOfNotifications = numberOfNotifications
        mNumberOfNotifications!!.text = getString(R.string.active_notifications,
            numberOfNotifications)
        Log.i(TAG, getString(R.string.active_notifications, numberOfNotifications))
    }

    // Unlikely in the sample, but the int will overflow if used enough so we skip the summary
    // ID. Most apps will prefer a more deterministic way of identifying an ID such as hashing
    // the content of the notification.
    /**
     * Retrieves a unique notification ID. We initialize `int notificationId` to our field
     * `sNotificationId` and increment `sNotificationId` for the next try. If
     * `notificationId` is equal to NOTIFICATION_GROUP_SUMMARY_ID (our summary group id) we
     * do this again in order to skip the use of NOTIFICATION_GROUP_SUMMARY_ID for a regular
     * notification. Finally we return `notificationId` to the caller.
     */
    val newNotificationId: Int
        get() {
            var notificationId = sNotificationId++

            // Unlikely in the sample, but the int will overflow if used enough so we skip the summary
            // ID. Most apps will prefer a more deterministic way of identifying an ID such as hashing
            // the content of the notification.
            if (notificationId == NOTIFICATION_GROUP_SUMMARY_ID) {
                notificationId = sNotificationId++
            }
            return notificationId
        }
    // Query the currently displayed notifications.

    // Since the notifications might include a summary notification remove it from the count if
    // it is present.
    /**
     * Returns the number of active notifications this activity has at the moment (minus the summary
     * group notification if it is present). We initialize `StatusBarNotification[] activeNotifications`
     * with the array returned by the `getActiveNotifications` method of `NotificationManager mNM`
     * (list of active notifications: ones that have been posted by the calling app that have not yet
     * been dismissed by the user or canceled by the app). Then we loop for each of the
     * `StatusBarNotification notification` and if the id of `notification` is the one
     * we use for our group summary (NOTIFICATION_GROUP_SUMMARY_ID) we return 1 less than the
     * length of `activeNotifications`. If none of the `StatusBarNotification` has that
     * id we return the length of `activeNotifications`.
     *
     * @return The number of active notifications this activity has at the moment (minus the summary
     * group notification if it is present).
     */
    private val numberOfNotifications: Int
        get() {
            // [BEGIN get_active_notifications]
            // Query the currently displayed notifications.
            val activeNotifications = mNM!!.activeNotifications
            // [END get_active_notifications]

            // Since the notifications might include a summary notification remove it from the count if
            // it is present.
            for (notification in activeNotifications) {
                if (notification.id == NOTIFICATION_GROUP_SUMMARY_ID) {
                    return activeNotifications.size - 1
                }
            }
            return activeNotifications.size
        }

    companion object {
        /**
         * Private request code for the `PendingIntent` we create to initialize our field
         * `PendingIntent mDeletePendingIntent` which we use as the delete `Intent` that is
         * broadcast when one of our notifications is deleted by the user. The request code can be any
         * number as long as it doesn't match another request code used in the same app.
         */
        private const val REQUEST_CODE = 2323

        /**
         * TAG used for logging
         */
        private const val TAG = "ActiveNotificationsFragment"

        /**
         * Key of the Notification group used by all the notifications we post.
         */
        private const val NOTIFICATION_GROUP = "com.example.android.activenotifications.notification_type"

        /**
         * The notification summary ID of the notification group summary used to group multiple notifications.
         */
        private const val NOTIFICATION_GROUP_SUMMARY_ID = 1

        /**
         * The id of the primary notification channel
         */
        const val PRIMARY_CHANNEL: String = "default"

        /**
         * Every notification needs a unique ID otherwise the previous one would be overwritten. This
         * variable is incremented when used.
         */
        private var sNotificationId = NOTIFICATION_GROUP_SUMMARY_ID + 1
    }
}