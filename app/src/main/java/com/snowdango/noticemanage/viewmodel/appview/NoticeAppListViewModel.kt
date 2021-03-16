package com.snowdango.noticemanage.viewmodel.appview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.noticemanage.model.AppData
import com.snowdango.noticemanage.model.ChannelData
import com.snowdango.noticemanage.model.usecase.RealmControlModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoticeAppListViewModel(private val model: RealmControlModel): ViewModel() {

    sealed class AppsDbResult{
        object None: AppsDbResult()
        data class Success(val apps: ArrayList<AppData>): AppsDbResult()
        data class Failed(val error: String): AppsDbResult()
    }

    sealed class ChannelDbResult{
        object None: ChannelDbResult()
        data class Success(val channels: ArrayList<ChannelData>): ChannelDbResult()
        data class Failed(val error: String): ChannelDbResult()
    }

    sealed class UpdateDbResult{
        object None: UpdateDbResult()
        object Success: UpdateDbResult()
        data class Failed(val error: String): UpdateDbResult()
    }

    val appsResult: LiveData<AppsDbResult> = MutableLiveData<AppsDbResult>().apply { value = AppsDbResult.None }
    val channelsResult: LiveData<ChannelDbResult> = MutableLiveData<ChannelDbResult>().apply { value = ChannelDbResult.None }
    val updateResult: LiveData<UpdateDbResult> = MutableLiveData<UpdateDbResult>().apply { value = UpdateDbResult.None }

    fun getAppsData() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = model.getApps()
            Log.d("dataViewModelApp",result.toString())
            viewModelScope.launch(Dispatchers.Main) {
                (appsResult as MutableLiveData<AppsDbResult>).value = when (result) {
                    is RealmControlModel.ModelGetAppsResult.Success -> AppsDbResult.Success(result.apps)
                    is RealmControlModel.ModelGetAppsResult.Failed -> AppsDbResult.Failed(result.error)
                }
            }
        }
    }

    fun getChannelsData() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = model.getChannels()
            Log.d("toggle data",result.toString())
            viewModelScope.launch(Dispatchers.Main) {
                (channelsResult as MutableLiveData<ChannelDbResult>).value = when (result) {
                    is RealmControlModel.ModelGetChannelsResult.Success -> ChannelDbResult.Success(result.channels)
                    is RealmControlModel.ModelGetChannelsResult.Failed -> ChannelDbResult.Failed(result.error)
                }
            }
        }
    }

    fun updateBlocked(channelId: String, blocked: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = model.updateBlocked(channelId, blocked)
            viewModelScope.launch(Dispatchers.Main) {
                (updateResult as MutableLiveData<UpdateDbResult>).value = when (result) {
                    is RealmControlModel.ModelUpDateResult.Success -> {
                        if(channelsResult.value is ChannelDbResult.Success){
                            (channelsResult.value as ChannelDbResult.Success).channels.find {
                                it.channelId == channelId
                            }?.blocked = blocked
                            Log.d("toggleBlock",blocked.toString())
                        }
                        UpdateDbResult.Success
                    }
                    is RealmControlModel.ModelUpDateResult.Failed -> UpdateDbResult.Failed(result.error)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}