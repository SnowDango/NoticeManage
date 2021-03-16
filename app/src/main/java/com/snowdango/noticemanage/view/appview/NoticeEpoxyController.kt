package com.snowdango.noticemanage.view.appview

import android.util.Log
import android.view.View
import com.airbnb.epoxy.Typed2EpoxyController
import com.snowdango.noticemanage.bindingadapter.ToggleAdapter
import com.snowdango.noticemanage.epoxyAppItem
import com.snowdango.noticemanage.epoxyChannelItem
import com.snowdango.noticemanage.model.AppData
import com.snowdango.noticemanage.model.ChannelData

class NoticeEpoxyController(
        private val toggleListener: ToggleListener
): Typed2EpoxyController<ArrayList<AppData>, ArrayList<ChannelData>>() {

    override fun buildModels(apps: ArrayList<AppData>?, channels: ArrayList<ChannelData>?) {
        apps?.forEach { noticeApp ->
            epoxyAppItem {
                id(noticeApp.packageName)
                icon(noticeApp.appIcon)
                noticeAppName(noticeApp.appName)
                click(View.OnClickListener {
                    noticeApp.toggleVisible()
                    setData(apps,channels)
                })
            }
            if(noticeApp.visibleChannel){
                channels?.filter { noticeChannel -> noticeChannel.packageName == noticeApp.packageName }?.forEach { channelData ->
                    epoxyChannelItem {
                        id(channelData.channelId)
                        channelName(channelData.channelId)
                        title(channelData.lastTitle)
                        text(channelData.lastText)
                        block(channelData.blocked)
                        switchChange(object :ToggleAdapter.SwitchToggle{
                            override fun toggleChange(isOn: Boolean) =
                                toggleListener.switchToggle(channelData.channelId,isOn)
                        })
                    }
                }
            }
        }
    }

    interface ToggleListener{
        fun switchToggle(channelId: String, isOn: Boolean)
    }
}