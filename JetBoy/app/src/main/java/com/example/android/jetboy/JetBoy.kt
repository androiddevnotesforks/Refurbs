/*
 * Copyright (C) 2009 The Android Open Source Project
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
// Android JET demonstration code:
// See the JetBoyView.java file for examples on the use of the JetPlayer class.
@file:Suppress("ReplaceNotNullAssertionWithElvisReturn")

package com.example.android.jetboy

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.android.jetboy.JetBoyView.JetBoyThread

/**
 * This is the main activity of this demo game.
 */
class JetBoy : Activity(), View.OnClickListener {
    /**
     * A handle to the thread that's actually running the animation.
     */
    private var mJetBoyThread: JetBoyThread? = null

    /**
     * A handle to the [View] in which the game is running.
     */
    private var mJetBoyView: JetBoyView? = null

    /**
     * the play start [Button]
     */
    private var mButton: Button? = null

    /**
     * [Button] used to hit retry
     */
    private var mButtonRetry: Button? = null

    /**
     * the [TextView] for instructions and such
     */
    private var mTextView: TextView? = null

    /**
     * game timer [TextView]
     */
    private var mTimerView: TextView? = null

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file [R.layout.main]. We initialize our [JetBoyView]
     * field [mJetBoyView] by finding the view with id R.id.JetBoyView, and our [JetBoyThread] field
     * [mJetBoyThread] with the value of the [JetBoyView.thread] property of [mJetBoyView] (it is
     * the thread that actually draws the animation). We initialize [Button] field [mButton] by
     * finding the view with id [R.id.Button01] ("START!") and set its [View.OnClickListener] to
     * 'this', initialize [Button] field [mButtonRetry] by finding the view with id [R.id.Button02]
     * ("RETRY") and set its [View.OnClickListener] to 'this'. We then initialize [TextView] field
     * [mTextView] by finding the view with id [R.id.text], and [TextView] field [mTimerView] by
     * finding the view with id [R.id.timer]. We call the [JetBoyView.setTimerView] method of
     * [mJetBoyView] to set the timer view widget it updates to [mTimerView], call the
     * [JetBoyView.setButtonView] method of [mJetBoyView] to set the button to start game over to
     * [mButtonRetry], and call the [JetBoyView.setTextView] method of [mJetBoyView] to set the end
     * game screen to [mTextView] (it reuses the help screen for this).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // get handles to the JetView from XML and the JET thread.
        mJetBoyView = findViewById(R.id.JetBoyView)
        mJetBoyThread = mJetBoyView!!.thread

        // look up the happy shiny button
        mButton = findViewById(R.id.Button01)
        mButton!!.setOnClickListener(this)
        mButtonRetry = findViewById(R.id.Button02)
        mButtonRetry!!.setOnClickListener(this)

        // set up handles for instruction text and game timer text
        mTextView = findViewById(R.id.text)
        mTimerView = findViewById(R.id.timer)
        mJetBoyView!!.setTimerView(mTimerView)
        mJetBoyView!!.setButtonView(mButtonRetry)
        mJetBoyView!!.setTextView(mTextView)
        mJetBoyView!!.setOnClickListener {
            if (mJetBoyThread != null) {
                dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER))
                dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER))
                mJetBoyThread!!.updateGameState()
            }
        }
    }

    /**
     * We implement [View.OnClickListener] and this is called when either of our buttons is clicked.
     * We branch on the [JetBoyThread.gameState] property of [mJetBoyThread], and if it is not
     * [STATE_START] or [STATE_PLAY] we then check if the [View] parameter [v] that was clicked is
     * our [Button] field [mButtonRetry]:
     *  * Game state is [STATE_START]: (the first screen) we set the text of [Button] field [mButton]
     *  to "PLAY!", set the visibility of [TextView] field [mTextView] to visible, set its text to
     *  the string with resource id [R.string.helpText] (the help text) and set the
     *  [JetBoyThread.gameState] property of [JetBoyThread] field [mJetBoyThread] to STATE_PLAY.
     *
     *  * Game state is [STATE_PLAY]: (we have entered game play, now we about to start running)
     *  We set the visibility of [Button] field [mButton] to INVISIBLE, set the visibility of
     *  [TextView] field [mTextView] to INVISIBLE, and set the visibility of [TextView] field
     *  [mTimerView] to VISIBLE. Then we set the game statue of `JetBoyThread` to STATE_RUNNING.
     *
     *  * The [View] parameter [v] that was clicked is [Button] field [mButtonRetry]: (this is a
     *  retry button click) We set the text of [TextView] field [mTextView] to the string with
     *  resource id [R.string.helpText] (the help text), set the text of [Button] field [mButton]
     *  to "PLAY!", and the visibility of [Button] field [mButtonRetry] to INVISIBLE. We set the
     *  visibility of [TextView] field [mTextView] to VISIBLE, set the text of [Button] field
     *  [mButton] to "PLAY!" (again?) and set its visibility to VISIBLE. The we set the
     *  [JetBoyThread.gameState] game state property of [JetBoyThread] field [mJetBoyThread] to
     *  [STATE_PLAY].
     *
     *  * If [View] paremter [v] is not one we were expecting to be clicked, we log the error.
     *
     * @param v The object which has been clicked
     */
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        // this is the first screen
        if (mJetBoyThread!!.gameState == STATE_START) {
            mButton!!.text = "PLAY!"
            mTextView!!.visibility = View.VISIBLE
            mTextView!!.setText(R.string.helpText)
            mJetBoyThread!!.gameState = STATE_PLAY
        } else if (mJetBoyThread!!.gameState == STATE_PLAY) {
            mButton!!.visibility = View.INVISIBLE
            mTextView!!.visibility = View.INVISIBLE
            mTimerView!!.visibility = View.VISIBLE
            mJetBoyThread!!.gameState = STATE_RUNNING
        } else if (mButtonRetry == v) {
            mTextView!!.setText(R.string.helpText)
            mButton!!.text = "PLAY!"
            mButtonRetry!!.visibility = View.INVISIBLE
            // mButtonRestart.setVisibility(View.INVISIBLE);
            mTextView!!.visibility = View.VISIBLE
            mButton!!.text = "PLAY!"
            mButton!!.visibility = View.VISIBLE
            mJetBoyThread!!.gameState = STATE_PLAY
        } else {
            Log.d("JB VIEW", "unknown click " + v.id)
            Log.d("JB VIEW", "state is  " + mJetBoyThread!!.mState)
        }
    }

    /**
     * Called when a key was pressed down and not handled by any of the views inside of the
     * activity. So, for example, key presses while the cursor is inside a [TextView] will not
     * trigger the event (unless it is a navigation to another object) because [TextView] handles
     * its own key presses. If our [Int] parameter [keyCode] is [KeyEvent.KEYCODE_BACK] (the Back
     * key) we return the value returned by our super's implementation of `onKeyDown`. For all
     * other keys we return the value returned by the [JetBoyThread.doKeyDown] method of
     * [JetBoyThread] field [mJetBoyThread].
     *
     * @param keyCode The value in [KeyEvent.getKeyCode].
     * @param msg     Description of the key event.
     * @return Return `true` to prevent this event from being propagated further, or `false` to
     * indicate that you have not handled this event and it should continue to be propagated.
     */
    override fun onKeyDown(keyCode: Int, msg: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, msg)
        } else {
            mJetBoyThread!!.doKeyDown(keyCode, msg)
        }
    }

    /**
     * Called when a key was released and not handled by any of the views inside of the activity.
     * So, for example, key presses while the cursor is inside a [TextView] will not trigger the
     * event (unless it is a navigation to another object) because [TextView] handles its own key
     * presses. If our [Int] parameter [keyCode] is [KeyEvent.KEYCODE_BACK] (the Back key) we return
     * the value returned by our super's implementation of `onKeyUp`. For all other keys we return
     * the value returned by the [JetBoyThread.doKeyUp] method of [JetBoyThread] field
     * [mJetBoyThread].
     *
     * @param keyCode The value in event.getKeyCode().
     * @param msg     Description of the key event.
     * @return Return `true` to prevent this event from being propagated further, or `false` to
     * indicate that you have not handled this event and it should continue to be propagated.
     */
    override fun onKeyUp(keyCode: Int, msg: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyUp(keyCode, msg)
        } else {
            mJetBoyThread!!.doKeyUp(keyCode, msg)
        }
    }
}