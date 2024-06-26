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
package com.example.android.insertingcells

import android.widget.ImageView
import android.widget.TextView

/**
 * The data model for every cell in the ListView for this application. This model stores
 * a title, an image resource and a default cell height for every item in the ListView.
 *
 * @param title       The title displayed in our [TextView], set by our constructor.
 * @param imgResource The resource id of the image displayed in our [ImageView], set by our constructor.
 * @param height      Cell height of this object, set by our constructor.
 */
class ListItemObject(val title: String, val imgResource: Int, val height: Int)