package com.snowdango.noticemanage.services.controller.notice

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.snowdango.noticemanage.NoticeManageApp
import com.snowdango.noticemanage.model.usecase.RealmControlModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.lang.Exception

class ListenNoticeService: NotificationListenerService(){

    private val model: RealmControlModel by inject()
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("NotificationBind", "now bind")
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d("Notification State","get Notification")
        val extras = sbn?.notification?.extras
        val channelId = sbn?.notification?.channelId.toString()
        val key = sbn?.key.toString()
        val packageName: String = sbn?.packageName.toString()
        val title = extras?.getString(Notification.EXTRA_TITLE).toString()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT).toString()
        val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT).toString()
        coroutineScope.launch(Dispatchers.IO){
            cancelNotice(channelId,key,packageName, title, text, subText)
        }
        super.onNotificationPosted(sbn)
    }

    private fun cancelNotice(channelId: String,key: String,packageName: String, title: String,text: String,subText: String){
        if(model.isChannelExist(channelId)){
            model.updateLastData(channelId,title,text,subText)
            if(model.isBlocked(channelId)) cancelNotification(key)
        }else{
            saveNotice(channelId, packageName, title, text, subText)
        }
    }

    private fun saveNotice(channelId: String,packageName: String,title: String,text: String,subText: String){
        val appName: String = try {
            val applicationInfo = NoticeManageApp.singletonContext().packageManager.getApplicationInfo(packageName,0)
            applicationInfo.loadLabel(NoticeManageApp.singletonContext().packageManager).toString()
        }catch (e: Exception){
            "undefinded app"
        }
        model.insert(channelId,appName,packageName,title,text,subText)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}