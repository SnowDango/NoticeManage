package com.snowdango.noticemanage.model

import android.graphics.drawable.Drawable
import com.snowdango.noticemanage.NoticeManageApp
import com.snowdango.noticemanage.R

data class AppData(
    val packageName: String,
    val appName: String,
    var appIcon: Drawable? = NoticeManageApp.singletonContext().getDrawable(R.drawable.ic_launcher_foreground),
    var visibleChannel: Boolean = false
){
    fun toggleVisible(){
        visibleChannel = visibleChannel.not()
    }
}
