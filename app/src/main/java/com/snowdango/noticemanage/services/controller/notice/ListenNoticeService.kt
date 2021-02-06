package com.snowdango.noticemanage.services.controller.notice

import android.app.Notification
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.snowdango.noticemanage.NoticeManageApp
import com.snowdango.noticemanage.model.usecase.RealmControlModel
import com.snowdango.noticemanage.repository.NoticeChannel
import io.realm.Realm
import org.koin.android.ext.android.inject
import java.lang.Exception

class ListenNoticeService: NotificationListenerService() {

    private val realm: Realm by lazy { Realm.getDefaultInstance() }
    private val model: RealmControlModel by inject()

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
        val packageName: String = sbn?.packageName.toString()
        val title = extras?.getString(Notification.EXTRA_TITLE).toString()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT).toString()
        val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT).toString()
        if(model.isChannelExist(channelId)){
            model.updateLastData(channelId,title,text,subText)
           if(model.isBlocked(channelId)){
               cancelNotification(sbn?.key)
           }
        }else{
            val appName: String = try {
                val applicationInfo = NoticeManageApp().packageManager.getApplicationInfo(packageName,0)
                applicationInfo.loadLabel(NoticeManageApp().packageManager).toString()
            }catch (e: Exception){
                "undefinded app"
            }
            model.insert(channelId,appName,packageName,title,text,subText)
        }
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {

        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}