package com.snowdango.noticemanage.view.appview

import com.airbnb.epoxy.Typed2EpoxyController
import com.snowdango.noticemanage.epoxyAppItem
import com.snowdango.noticemanage.epoxyChannelItem
import com.snowdango.noticemanage.model.AppData
import com.snowdango.noticemanage.model.ChannelData

class NoticeEpoxyController: Typed2EpoxyController<ArrayList<AppData>, ArrayList<ChannelData>>() {

    override fun buildModels(apps: ArrayList<AppData>?, channels: ArrayList<ChannelData>?) {
        apps?.forEach { noticeApp ->
            epoxyAppItem {
                id(noticeApp.packageName)
                appName(noticeApp.appName)
            }
            if(noticeApp.visibleChannel){
                channels?.filter { noticeChannel -> noticeChannel.packageName == noticeApp.packageName }?.forEach { channelData ->
                    epoxyChannelItem {
                        id(channelData.channelId)
                        channelName(channelData.channelId)
                    }
                }
            }
        }
    }

}