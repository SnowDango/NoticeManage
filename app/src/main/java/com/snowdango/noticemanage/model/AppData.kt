package com.snowdango.noticemanage.model

import android.graphics.drawable.Drawable
import com.snowdango.noticemanage.NoticeManageApp
import com.snowdango.noticemanage.R

data class AppData(
    val packageName: String,
    val appName: String,
    var aooIcon: Drawable? = NoticeManageApp().getDrawable(R.drawable.ic_launcher_foreground),
    var visibleChannel: Boolean = false
)
