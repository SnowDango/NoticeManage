package com.snowdango.noticemanage

import android.app.Application
import com.snowdango.noticemanage.model.usecase.RealmControlModel
import com.snowdango.noticemanage.viewmodel.appview.NoticeAppListViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class NoticeManageApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().name("notice-app.realm").build())
        startKoin {
            module { modelModule }
            module { viewModule }
        }
    }

    private val modelModule: Module = module {
        factory { RealmControlModel() }
    }

    private val viewModule: Module = module {
        viewModel { NoticeAppListViewModel(get()) }
    }
}