package com.snowdango.noticemanage.model.usecase

import android.graphics.drawable.Drawable
import android.util.Log
import com.snowdango.noticemanage.NoticeManageApp
import com.snowdango.noticemanage.R
import com.snowdango.noticemanage.model.AppData
import com.snowdango.noticemanage.model.ChannelData
import com.snowdango.noticemanage.repository.NoticeApp
import com.snowdango.noticemanage.repository.NoticeChannel
import io.realm.Realm

class RealmControlModel() {

    sealed class ModelGetAppsResult{
        data class Success(val apps: ArrayList<AppData>): ModelGetAppsResult()
        data class Failed(val error: String): ModelGetAppsResult()
    }
    sealed class ModelGetChannelsResult{
        data class Success(val channels: ArrayList<ChannelData>): ModelGetChannelsResult()
        data class Failed(val error: String):ModelGetChannelsResult()
    }
    sealed class ModelUpDateResult{
        object Success: ModelUpDateResult()
        data class Failed(val error: String): ModelUpDateResult()
    }

    init {
        Realm.init(NoticeManageApp.singletonContext())
    }

    fun isChannelExist(channelId: String): Boolean{
        val realm = Realm.getDefaultInstance()
        val channels = realm.where(NoticeChannel::class.java).equalTo("channelId",channelId).findAll()
        val isExist = !channels.isNullOrEmpty()
        realm.close()
        return isExist
    }

    private fun isAppExist(packageName: String): Boolean{
        val realm = Realm.getDefaultInstance()
        val apps = realm.where(NoticeApp::class.java).equalTo("packageName",packageName).findAll()
        val isExist = !apps.isNullOrEmpty()
        realm.close()
        return isExist
    }

    fun insert(channelId: String,appName: String, packageName: String, title: String, text: String, subText: String){
        if(!isAppExist(packageName)){
            appInsert(appName, packageName)
        }
        channelInsert(channelId, packageName, title, text, subText)
    }

    private fun appInsert(appName: String,packageName: String){
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val app = it.createObject(NoticeApp::class.java,packageName)
            app.appName = appName
            realm.copyFromRealm(app)
        }
        realm.close()
    }

    private fun channelInsert(channelId: String,packageName: String,title: String,text: String,subText: String){
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val channel = it.createObject(NoticeChannel::class.java,channelId)
            channel.packageName = packageName
            channel.lastTitle = title
            channel.lastText = text
            channel.lastSubText = subText
            realm.copyFromRealm(channel)
        }
        realm.close()
    }

    fun updateLastData(channelId: String,title: String,text: String,subText: String){
        val realm = Realm.getDefaultInstance()
        val targetChannel = realm.where(NoticeChannel::class.java).equalTo("channelId", channelId).findFirst()
        realm.executeTransaction {
            targetChannel?.lastTitle = title
            targetChannel?.lastText = text
            targetChannel?.lastSubText = subText
        }
        realm.close()
    }

    fun updateBlocked(channelId: String,blocked: Boolean): ModelUpDateResult {
        val realm = Realm.getDefaultInstance()
        return try {
            val targetChannel =
                realm.where(NoticeChannel::class.java).equalTo("channelId", channelId).findFirst()
            val blockedStateBool2Int = if (blocked) 0 else 1
            realm.executeTransaction {
                targetChannel?.blocked = blockedStateBool2Int
            }
            realm.close()
            ModelUpDateResult.Success
        }catch (e: Exception){
            realm.close()
            ModelUpDateResult.Failed(e.toString())
        }
    }

    fun deleteApp(packageName: String){
        val realm = Realm.getDefaultInstance()
        val targetApp = realm.where(NoticeApp::class.java).equalTo("packageName",packageName).findFirst()
        val targetChannels = realm.where(NoticeChannel::class.java).equalTo("packageName",packageName).findAll()
        realm.executeTransaction {
            targetApp?.deleteFromRealm()
            targetChannels.deleteAllFromRealm()
        }
        realm.close()
    }

    fun isBlocked(channelId: String): Boolean{
        val realm = Realm.getDefaultInstance()
        val targetChannel = realm.where(NoticeChannel::class.java).equalTo("channelId",channelId).findFirst()
        val isBlock = targetChannel?.blocked != 1
        realm.close()
        return isBlock
    }

    fun getApps(): ModelGetAppsResult {
        val realm = Realm.getDefaultInstance()
        return try {
            val apps = realm.where(NoticeApp::class.java).findAll()
            val returnArrayList = arrayListOf<AppData>()
            apps.forEach { noticeApp ->
                val icon: Drawable? = try {
                    NoticeManageApp.singletonContext().packageManager.getApplicationIcon(noticeApp.packageName.toString())
                } catch (e: Exception) {
                    NoticeManageApp.singletonContext().getDrawable(R.drawable.ic_launcher_foreground)
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
            realm.close()
            ModelGetAppsResult.Success(returnArrayList)
        }catch (e: Exception){
            realm.close()
            ModelGetAppsResult.Failed(e.toString())
        }
    }

    fun getChannels(): ModelGetChannelsResult{
        val realm = Realm.getDefaultInstance()
        return try {
            val channels = realm.where(NoticeChannel::class.java).findAll()
            val returnArrayList = arrayListOf<ChannelData>()
            channels.forEach { noticeChannel ->
                val blockedStateInt2Bool = noticeChannel.blocked == 0
                Log.d("toggle channel info", noticeChannel.toString())
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
            realm.close()
            ModelGetChannelsResult.Success(returnArrayList)
        }catch (e: Exception){
            realm.close()
            ModelGetChannelsResult.Failed(e.toString())
        }
    }

}