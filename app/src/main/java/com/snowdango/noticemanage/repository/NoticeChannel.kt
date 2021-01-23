package com.snowdango.noticemanage.repository

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class NoticeChannel(
    @PrimaryKey var channelId: String? = null,
    @Required var packageName: String = "",
    var lastTitle: String = "",
    var lastText: String = "",
    var lastSubText: String = ""
):RealmObject() {}