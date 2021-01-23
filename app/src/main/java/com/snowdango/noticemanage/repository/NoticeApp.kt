package com.snowdango.noticemanage.repository

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class NoticeApp(
    @PrimaryKey var packageName: String? = null,
    @Required var appName: String = ""
): RealmObject() {}