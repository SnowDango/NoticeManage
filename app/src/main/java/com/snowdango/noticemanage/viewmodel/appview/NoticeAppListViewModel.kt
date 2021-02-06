package com.snowdango.noticemanage.viewmodel.appview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowdango.noticemanage.model.usecase.RealmControlModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoticeAppListViewModel(private val model: RealmControlModel): ViewModel() {

    val appsResult: LiveData<RealmControlModel.ModelGetAppsResult> =
        MutableLiveData<RealmControlModel.ModelGetAppsResult>().apply { value = RealmControlModel.ModelGetAppsResult.None }
    val channelsResult: LiveData<RealmControlModel.ModelGetChannelsResult> =
        MutableLiveData<RealmControlModel.ModelGetChannelsResult>().apply { value = RealmControlModel.ModelGetChannelsResult.None }
    val updateResult: LiveData<RealmControlModel.ModelUpDateResult> =
        MutableLiveData<RealmControlModel.ModelUpDateResult>().apply { value = RealmControlModel.ModelUpDateResult.None }

    fun getAppsData(){
        viewModelScope.launch(Dispatchers.Default) {
            val result = model.getApps()
            viewModelScope.launch(Dispatchers.Main) {
                (appsResult as MutableLiveData<RealmControlModel.ModelGetAppsResult>).value = result
            }
        }
    }

    fun getChannelsData(){
        viewModelScope.launch(Dispatchers.Default) {
            val result = model.getChannels()
            viewModelScope.launch(Dispatchers.Main){
                (channelsResult as MutableLiveData<RealmControlModel.ModelGetChannelsResult>).value = result
            }
        }
    }

    fun updateBlocked(channelId: String,blocked: Boolean){
        viewModelScope.launch(Dispatchers.Default){
            val result = model.updateBlocked(channelId, blocked)
            viewModelScope.launch(Dispatchers.Main){
                (updateResult as MutableLiveData<RealmControlModel.ModelUpDateResult>).value = result
            }
        }
    }

}