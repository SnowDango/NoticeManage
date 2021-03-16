package com.snowdango.noticemanage.model

data class ChannelData(
    val channelId: String,
    val packageName: String,
    val lastTitle: String,
    val lastText: String,
    val lastSubText: String,
    var blocked: Boolean
)
