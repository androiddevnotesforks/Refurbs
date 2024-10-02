/*
* Copyright 2013 The Android Open Source Project
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
@file:Suppress("ReplaceNotNullAssertionWithElvisReturn")

package com.example.android.revealeffectbasic

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import android.widget.ViewAnimator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.android.common.activities.SampleActivityBase
import com.example.android.common.logger.Log
import com.example.android.common.logger.LogFragment
import com.example.android.common.logger.LogNode
import com.example.android.common.logger.LogView
import com.example.android.common.logger.LogWrapper
import com.example.android.common.logger.MessageOnlyLogFilter

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * [Fragment] which can display a view.
 *
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
class MainActivity : SampleActivityBase() {
    /**
     * Whether the Log Fragment is currently shown
     */
    private var mLogShown = false

    /**
     * Called when the activity is starting. We first call through to our super's implementation of
     * `onCreate`, then we set our content view to our layout file [R.layout.activity_main]. If our
     * [Bundle] parameter [savedInstanceState] is `null`, this is the first time we were called so
     * we use the [FragmentManager] for interacting with fragments associated with this activity to
     * begin [FragmentTransaction] variable `val transaction`, initialize [RevealEffectBasicFragment]
     * variable `val fragment` with a new instance and use `transaction` to replace (add) `fragment`
     * to the container view with ID [R.id.sample_content_fragment in our layout]. We then commit
     * `transaction`.
     *
     * @param savedInstanceState If this is `null` we need to create and add our
     * [RevealEffectBasicFragment] fragment, if not `null` we are being recreated after a
     * configuration change and the fragment will be restored by the system.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        // TODO: Move text automatically, instead of using android:layout_marginTop in xml file
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<LinearLayout>(R.id.sample_main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
                topMargin = insets.top
                bottomMargin = insets.bottom
            }
            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = RevealEffectBasicFragment()
            transaction.replace(R.id.sample_content_fragment, fragment)
            transaction.commit()
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. We use a [MenuInflater] for
     * this context to inflate our menu layout file [R.menu.main] into our [Menu] parameter [menu]
     * and return `true` so that the menu will be displayed.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return `true` for the menu to be displayed;
     * if you return `false` it will not be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * Prepare the Screen's standard options menu to be displayed. We initialize [MenuItem] variable
     * `val logToggle` by finding the menu item in our [Menu] parameter [menu] with id
     * [R.id.menu_toggle_log], set it to visible only if the view in our layout with id
     * [R.id.sample_output] is an instance of [ViewAnimator], and set its title to
     * [R.string.sample_hide_log] ("Hide Log") if our [Boolean] flag field [mLogShown] is `true` or
     * to [R.string.sample_show_log] ("Show Log") if it is `false`. Finally we return the value
     * returned by our super's implementation of `onPrepareOptionsMenu` to the caller.
     *
     * @param menu The options menu as last shown or first initialized by [onCreateOptionsMenu].
     * @return You must return `true` for the menu to be displayed;
     * if you return `false` it will not be shown.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val logToggle: MenuItem = menu.findItem(R.id.menu_toggle_log)
        logToggle.isVisible = findViewById<View>(R.id.sample_output) is ViewAnimator
        logToggle.setTitle(if (mLogShown) R.string.sample_hide_log else R.string.sample_show_log)
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. We use an `if`
     * statement to handle only the [MenuItem] with id [R.id.menu_toggle_log]. If it is that item
     * we toggle the value of [Boolean] flag field [mLogShown], then initialize [ViewAnimator]
     * variable `val output` by finding the view with ID [R.id.sample_output]. If [mLogShown] is
     * `true` we set the displayed child of `output` to 1, if it is `false` we we set the displayed
     * child of `output` to 0. We then call the method [invalidateOptionsMenu] to declare that the
     * options menu has changed, so should be recreated, and return `true` to the caller to consume
     * the event here. If the item id is not [R.id.menu_toggle_log] we return the value returned by
     * our super's implementation of `onOptionsItemSelected`.
     *
     * @param item The menu item that was selected.
     * @return [Boolean] Return `false` to allow normal menu processing to
     * proceed, `true` to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_toggle_log) {
            mLogShown = !mLogShown
            val output: ViewAnimator = findViewById(R.id.sample_output)
            if (mLogShown) {
                output.displayedChild = 1
            } else {
                output.displayedChild = 0
            }
            invalidateOptionsMenu()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Create a chain of targets that will receive log data. We initialize [LogWrapper] variable
     * `val logWrapper` with a new instance, and set it as the [LogNode] that log data will be sent
     * to. We create a new instance for [MessageOnlyLogFilter] variable `val msgFilter` (strips out
     * everything except the message text) and set it as the [LogNode] that `logWrapper` will next
     * send data to. We then initialize [LogFragment] variable `val logFragment` by using the
     * [FragmentManager] for interacting with fragments associated with this activity to find the
     * fragment with the resource id [R.id.log_fragment], then set its [LogView] as the [LogNode]
     * that `msgFilter` will send data to. Finally we log the message "Ready".
     */
    override fun initializeLogging() {
        // Wraps Android's native log framework.
        val logWrapper = LogWrapper()
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.logNode = logWrapper

        // Filter strips out everything except the message text.
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter

        // On screen logging via a fragment with a TextView.
        val logFragment = supportFragmentManager
            .findFragmentById(R.id.log_fragment) as LogFragment?
        msgFilter.next = logFragment!!.logView
        Log.i(TAG, "Ready")
    }

    companion object {
        /**
         * TAG used for logging
         */
        const val TAG: String = "MainActivity"
    }
}