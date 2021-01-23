package com.snowdango.noticemanage.viewmodel.appview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class NoticeAppListViewModel: ViewModel() {

    val coroutineScope: CoroutineScope = CoroutineScope( Dispatchers.IO )

    fun getData(){

    }

}