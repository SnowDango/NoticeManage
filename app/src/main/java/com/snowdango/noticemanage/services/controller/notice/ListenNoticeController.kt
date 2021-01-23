package com.snowdango.noticemanage.services.controller.notice

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.snowdango.noticemanage.repository.NoticeChannel
import io.realm.Realm

class ListenNoticeController: NotificationListenerService() {

    private val realm: Realm by lazy { Realm.getDefaultInstance() }

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
        val channelId = sbn?.notification?.channelId
        val packageName: String = sbn?.packageName.toString()
        val title = extras?.getString(Notification.EXTRA_TITLE)
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)
        val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)
        Log.d(
            "NotificationInfo", "" +
                    "channelId:${sbn?.notification?.channelId} \n " +
                    "key:${sbn?.key} \n" +
                    "appName:${sbn?.packageName}\n" +
                    "Title:${title} \n " +
                    "Text:${text} \n" +
                    "subText:${subText}"
        )
        insert(channelId.toString(),packageName,title.toString(),text.toString(),subText.toString())
        show()
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun insert(channelId: String, packageName: String, title: String, text: String, subText: String){
        realm.executeTransactionAsync{
            val channel = it.createObject(NoticeChannel::class.java,channelId)
            channel.packageName = packageName
            channel.lastTitle = title
            channel.lastText = text
            channel.lastSubText = subText
            it.copyFromRealm(channel)
        }
    }

    private fun show(){
        realm.executeTransactionAsync{
            val channels = it.where<NoticeChannel>(NoticeChannel::class.java).findAll()
            channels.forEach {
                Log.d("NotificationChannel",it.channelId.toString())
            }
        }

    }
    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}