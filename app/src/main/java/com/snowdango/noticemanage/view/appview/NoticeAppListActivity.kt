package com.snowdango.noticemanage.view.appview

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.snowdango.noticemanage.R
import com.snowdango.noticemanage.services.controller.notice.ListenNoticeService
import com.snowdango.noticemanage.viewmodel.appview.NoticeAppListViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class NoticeAppListActivity : AppCompatActivity(){

    private val noticeAppListViewModel: NoticeAppListViewModel by viewModel()
    private val noticeEpoxyController: NoticeEpoxyController by lazy { NoticeEpoxyController(
        object : NoticeEpoxyController.ToggleListener{
            override fun switchToggle(channelId: String, isOn: Boolean) {
                noticeAppListViewModel.updateBlocked(channelId,isOn)
            }
        }
    )}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_appview)
        
        if(NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName).not()) {
            val noticeListenSetting = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(noticeListenSetting)
        }else {
            startService(Intent(applicationContext, ListenNoticeService::class.java))
        }

        noticeEpoxyController.setFilterDuplicates(true)
        findViewById<EpoxyRecyclerView>(R.id.epoxyRecyclerView).also {
            it.adapter = noticeEpoxyController.adapter
            it.layoutManager = LinearLayoutManager(this).also { linearLayoutManager ->
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    override fun onStart() {
        setObserver()
        noticeAppListViewModel.getAppsData()
        noticeAppListViewModel.getChannelsData()
        super.onStart()
    }

    private fun setObserver(){
        noticeAppListViewModel.appsResult.observe(this, Observer { result ->
            when(result){
                is NoticeAppListViewModel.AppsDbResult.None -> return@Observer
                is NoticeAppListViewModel.AppsDbResult.Success ->
                    if(noticeAppListViewModel.channelsResult.value is NoticeAppListViewModel.ChannelDbResult.Success) epoxySetData()
                is NoticeAppListViewModel.AppsDbResult.Failed -> errorShow(result.error)
            }
        })
        noticeAppListViewModel.channelsResult.observe(this, Observer { result ->
            when(result){
                is NoticeAppListViewModel.ChannelDbResult.None -> return@Observer
                is NoticeAppListViewModel.ChannelDbResult.Success ->
                    if(noticeAppListViewModel.appsResult.value is NoticeAppListViewModel.AppsDbResult.Success) epoxySetData()
                is NoticeAppListViewModel.ChannelDbResult.Failed -> errorShow(result.error)
            }
        })
        noticeAppListViewModel.updateResult.observe(this, Observer{ result ->
            when(result){
                is NoticeAppListViewModel.UpdateDbResult.None -> return@Observer
                is NoticeAppListViewModel.UpdateDbResult.Success ->
                    Toast.makeText(this, "update complete", Toast.LENGTH_SHORT).show()
                is NoticeAppListViewModel.UpdateDbResult.Failed -> {
                    errorShow(result.error)
                    noticeEpoxyController.requestModelBuild()
                }
            }
        })
    }

    private fun epoxySetData(){
        noticeEpoxyController.setData(
                (noticeAppListViewModel.appsResult.value as NoticeAppListViewModel.AppsDbResult.Success).apps,
                (noticeAppListViewModel.channelsResult.value as NoticeAppListViewModel.ChannelDbResult.Success).channels
        )
    }

    private fun errorShow(error: String){
        Toast.makeText(this,error,Toast.LENGTH_LONG).show()
        Log.d("errorLog", error)
    }
}
