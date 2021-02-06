package com.snowdango.noticemanage.view.appview

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.snowdango.noticemanage.R
import com.snowdango.noticemanage.services.controller.notice.ListenNoticeService
import com.snowdango.noticemanage.viewmodel.appview.NoticeAppListViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class NoticeAppListActivity : AppCompatActivity(){

    private val noticeAppListViewModel: NoticeAppListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_appview)
        
        if(NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName).not()) {
            Log.d("access notification permission","Not Permission")
            val noticeListenSetting = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(noticeListenSetting)
        }else{
            Log.d("access notification permission","Permission　Success")
            startService(Intent(applicationContext,ListenNoticeService::class.java))
        }


    }
}