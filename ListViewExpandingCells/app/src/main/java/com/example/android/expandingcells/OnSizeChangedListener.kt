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
package com.example.android.expandingcells

import android.view.View

/**
 * A listener used to update the list data object when the corresponding expanding
 * layout experiences a size change.
 */
interface OnSizeChangedListener {
    /**
     * Called from the [View.onSizeChanged] override of the [ExpandingLayout]
     *
     * @param newHeight new height of the [View]
     */
    fun onSizeChanged(newHeight: Int)
}