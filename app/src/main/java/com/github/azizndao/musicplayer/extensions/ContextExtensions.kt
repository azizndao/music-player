/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.extensions

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.AttrRes

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
  return getSystemService(name) as T
}

fun Context.getColorByTheme(
  @AttrRes id: Int
): Int {
  val colorAttr: Int? = id
  val outValue = TypedValue()
  theme.resolveAttribute(colorAttr!!, outValue, true)
  return outValue.data
}

fun Context.shortToast(message: String) = Toast.makeText(this, message, LENGTH_SHORT).show()