package com.snowdango.noticemanage.model.usecase

import com.snowdango.noticemanage.NoticeManageApp
import com.snowdango.noticemanage.R
import com.snowdango.noticemanage.model.AppData
import com.snowdango.noticemanage.model.ChannelData
import com.snowdango.noticemanage.repository.NoticeApp
import com.snowdango.noticemanage.repository.NoticeChannel
import io.realm.Realm

class RealmControlModel {

    sealed class ModelGetAppsResult{
        object None: ModelGetAppsResult()
        data class Success(val apps: ArrayList<AppData>): ModelGetAppsResult()
        data class Failed(val error: String): ModelGetAppsResult()
    }
    sealed class ModelGetChannelsResult{
        object None: ModelGetChannelsResult()
        data class Success(val channels: ArrayList<ChannelData>): ModelGetChannelsResult()
        data class Failed(val error: String):ModelGetChannelsResult()
    }
    sealed class ModelUpDateResult{
        object None: ModelUpDateResult()
        object Success: ModelUpDateResult()
        data class Failed(val error: String): ModelUpDateResult()
    }

    private val realm by lazy { Realm.getDefaultInstance() }

    fun isChannelExist(channelId: String): Boolean{
        val channels = realm.where(NoticeChannel::class.java).equalTo("channelId",channelId).findAll()
        return !channels.isNullOrEmpty()
    }

    private fun isAppExist(packageName: String): Boolean{
        val apps = realm.where(NoticeApp::class.java).equalTo("packageName",packageName).findAll()
        return !apps.isNullOrEmpty()
    }

    fun insert(channelId: String,appName: String, packageName: String, title: String, text: String, subText: String){
        if(isAppExist(packageName)){
            appInsert(appName, packageName)
        }
        channelInsert(channelId, packageName, title, text, subText)
    }

    private fun appInsert(appName: String,packageName: String){
        realm.executeTransactionAsync {
            val app = it.createObject(NoticeApp::class.java)
            app.appName = appName
            app.packageName = packageName
            realm.copyFromRealm(app)
        }
    }

    private fun channelInsert(channelId: String,packageName: String,title: String,text: String,subText: String){
        realm.executeTransactionAsync {
            val channel = it.createObject(NoticeChannel::class.java)
            channel.channelId = channelId
            channel.packageName = packageName
            channel.lastTitle = title
            channel.lastText = text
            channel.lastSubText = subText
            realm.copyFromRealm(channel)
        }
    }

    fun updateLastData(channelId: String,title: String,text: String,subText: String){
        val targetChannel = realm.where(NoticeChannel::class.java).equalTo("channelId", channelId).findFirst()
        realm.executeTransactionAsync {
            targetChannel?.lastTitle = title
            targetChannel?.lastText = text
            targetChannel?.lastSubText = subText
        }
    }

    fun updateBlocked(channelId: String,blocked: Boolean): ModelUpDateResult {
        return try {
            val targetChannel =
                realm.where(NoticeChannel::class.java).equalTo("channelId", channelId).findFirst()
            val blockedStateBool2Int = if (blocked) 0 else 1
            realm.executeTransactionAsync {
                targetChannel?.blocked = blockedStateBool2Int
            }
            ModelUpDateResult.Success
        }catch (e: Exception){
            ModelUpDateResult.Failed(e.toString())
        }
    }

    fun deleteApp(packageName: String){
        val targetApp = realm.where(NoticeApp::class.java).equalTo("packageName",packageName).findFirst()
        val targetChannels = realm.where(NoticeChannel::class.java).equalTo("packageName",packageName).findAll()
        realm.executeTransactionAsync {
            targetApp?.deleteFromRealm()
            targetChannels.deleteAllFromRealm()
        }
    }

    fun isBlocked(channelId: String): Boolean{
        val targetChannel = realm.where(NoticeChannel::class.java).equalTo("channelId",channelId).findFirst()
        return targetChannel?.blocked != 1
    }

    fun getApps(): ModelGetAppsResult {
        return try {
            val apps = realm.where(NoticeApp::class.java).findAll()
            val returnArrayList = arrayListOf<AppData>()
            apps.forEach { noticeApp ->
                val icon = try {
                    NoticeManageApp().packageManager.getApplicationIcon(noticeApp.packageName.toString())
                } catch (e: Exception) {
                    NoticeManageApp().getDrawable(R.drawable.ic_launcher_foreground)
                }
                returnArrayList.add(
                    AppData(
                        noticeApp.packageName.toString(),
                        noticeApp.appName,
                        icon,
                        visibleChannel = false
                    )
                )
            }
            ModelGetAppsResult.Success(returnArrayList)
        }catch (e: Exception){
            ModelGetAppsResult.Failed(e.toString())
        }
    }

    fun getChannels(): ModelGetChannelsResult{
        return try {
            val channels = realm.where(NoticeChannel::class.java).findAll()
            val returnArrayList = arrayListOf<ChannelData>()
            channels.forEach { noticeChannel ->
                val blockedStateInt2Bool = noticeChannel.blocked == 0
                returnArrayList.add(
                    ChannelData(
                        noticeChannel.channelId.toString(),
                        noticeChannel.packageName,
                        noticeChannel.lastTitle,
                        noticeChannel.lastText,
                        noticeChannel.lastSubText,
                        blockedStateInt2Bool
                    )
                )
            }
            ModelGetChannelsResult.Success(returnArrayList)
        }catch (e: Exception){
            ModelGetChannelsResult.Failed(e.toString())
        }
    }
}