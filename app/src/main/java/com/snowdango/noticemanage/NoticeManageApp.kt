package com.snowdango.noticemanage

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class NoticeManageApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().name("notice-app.realm").build())
    }
}