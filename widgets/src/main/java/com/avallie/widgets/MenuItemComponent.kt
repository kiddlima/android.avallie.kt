package com.avallie.widgets

import android.graphics.drawable.Drawable
import android.view.View

class MenuItemComponent(
    val title: String,
    val description: String,
    val icon: Drawable,
    val onClick: View.OnClickListener
)