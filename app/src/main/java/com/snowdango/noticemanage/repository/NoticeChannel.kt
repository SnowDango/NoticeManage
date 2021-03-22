package com.snowdango.noticemanage.repository

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class NoticeChannel(
    @PrimaryKey var channelId: String? = null,
    @Required var packageName: String = "",
    var lastTitle: String = "",
    var lastText: String = "",
    var lastSubText: String = "",
    var blocked: Int = 0 // 0: not blocked, 1: blocked
):RealmObject() {}