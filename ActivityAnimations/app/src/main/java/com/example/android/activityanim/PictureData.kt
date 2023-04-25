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
package com.example.android.activityanim

import android.graphics.Bitmap

/**
 * Container for information about the thumbnail pictures displayed by `ActivityAnimations` in
 * its `GridView`.
 */
class PictureData
/**
 * Our constructor, just saves its parameters in their respective fields
 *
 * @param resourceId resource id of the jpg we represent.
 * @param description random description string for this jpg
 * @param thumbnail thumbnail version of the jpg to display in the `GridView` of
 * `ActivityAnimations`
 */(
    /**
     * Resource id of the jpg used to create the thumbnail, and which will be used by the activity
     * `PictureDetailsActivity` to display the full sized jpg
     */
    var resourceId: Int,
    /**
     * Description string to be displayed by `PictureDetailsActivity`
     */
    var description: String,
    /**
     * Thumbnail image that `ActivityAnimations` displays in its `GridView`.
     */
    var thumbnail: Bitmap)