/*
 * Copyright (C) 2013 The Android Open Source Project
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
@file:Suppress("ReplaceNotNullAssertionWithElvisReturn")

package com.example.android.expandingcells

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

/**
 * This activity creates a ListView whose items can be clicked to expand and show
 * additional content.
 *
 *
 * In this specific demo, each item in a ListView displays an image and a corresponding title. These
 * two items are centered in the default (collapsed) state of the [ExpandingListView]'s item. When
 * the item is clicked, it expands to display text of some varying length. The item persists in this
 * expanded state (even if the user scrolls away and then scrolls back to the same location) until
 * it is clicked again, at which point the cell collapses back to its default state.
 *
 * See: [ListViewExpandingCells](https://www.youtube.com/watch?v=mwE61B56pVQ)
 */
class ExpandingCells : ComponentActivity() {
    /**
     * [ExpandingListView] in our layout with id `R.id.main_list_view` (it is the only widget in
     * our layout, and extends [ListView]).
     */
    private var mListView: ExpandingListView? = null

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we set our content view to our layout file `R.layout.activity_main`. We initialize our
     * [Array] of [ExpandableListItem] variable `val values` with three different instances. We
     * initialize our [MutableList] of [ExpandableListItem] variable `val mData` with a new instance
     * of [ArrayList]. We then loop over [Int] variable `var i` adding [NUM_OF_CELLS] of
     * [ExpandableListItem] objects constructed by copying the contents of entries of `values` in a
     * round robin order. We initialize [CustomArrayAdapter] variable `val adapter` with a new
     * instance which uses `mData` as its dataset, and the layout file `R.layout.list_view_item` to
     * display each item in that dataset. We initialize [ExpandingListView] field [mListView] by
     * finding the view with id `R.id.main_list_view`, set its adapter to be `adapter` and call its
     * [ExpandingListView.setDivider] method (kotlin `divider` property) to set its divider to `null`.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<ExpandingListView>(R.id.main_list_view)
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

        val values: Array<ExpandableListItem> = arrayOf(
            ExpandableListItem("Chameleon", R.drawable.chameleon, CELL_DEFAULT_HEIGHT,
                resources.getString(R.string.short_lorem_ipsum)),
            ExpandableListItem("Rock", R.drawable.rock, CELL_DEFAULT_HEIGHT,
                resources.getString(R.string.medium_lorem_ipsum)),
            ExpandableListItem("Flower", R.drawable.flower, CELL_DEFAULT_HEIGHT,
                resources.getString(R.string.long_lorem_ipsum)))
        val mData: MutableList<ExpandableListItem> = ArrayList()
        for (i in 0 until NUM_OF_CELLS) {
            val obj = values[i % values.size]
            mData.add(ExpandableListItem(obj.title, obj.imgResource,
                obj.collapsedHeight, obj.text))
        }
        val adapter = CustomArrayAdapter(this, R.layout.list_view_item, mData)
        mListView = rootView
        mListView!!.adapter = adapter
        mListView!!.divider = null
    }

    companion object {
        /**
         * Used as the collapsed height of the [ExpandableListItem] objects we create.
         */
        private const val CELL_DEFAULT_HEIGHT = 200

        /**
         * Number of [ExpandableListItem] objects we add to our dataset.
         */
        private const val NUM_OF_CELLS = 30
    }
}
