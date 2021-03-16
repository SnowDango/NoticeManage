package com.snowdango.noticemanage.bindingadapter

import androidx.databinding.BindingAdapter
import com.github.angads25.toggle.widget.LabeledSwitch

object SwitchPositionAdapter {

    @JvmStatic
    @BindingAdapter("setOn")
    fun LabeledSwitch.setInitOn(initOn: Boolean){
        this.isOn = initOn
    }
}