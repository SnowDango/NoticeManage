package com.snowdango.noticemanage.view.appview

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.snowdango.noticemanage.R
import com.snowdango.noticemanage.services.controller.notice.ListenNoticeController
import com.snowdango.noticemanage.viewmodel.appview.NoticeAppListViewModel

class NoticeAppListActivity : AppCompatActivity(){

    private val noticeAppListViewModel: NoticeAppListViewModel by viewModels<NoticeAppListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName).not()) {
            Log.d("access notification permission","Not Permission")
            val noticeListenSetting = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(noticeListenSetting)
        }else{
            Log.d("access notification permission","Permission　Success")
            startService(Intent(applicationContext,ListenNoticeController::class.java))
        }
    }
}