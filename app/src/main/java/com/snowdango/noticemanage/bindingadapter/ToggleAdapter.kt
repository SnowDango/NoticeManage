package com.snowdango.noticemanage.bindingadapter

import androidx.databinding.BindingAdapter
import com.github.angads25.toggle.widget.LabeledSwitch

object ToggleAdapter {

    @JvmStatic
    @BindingAdapter("onToggle")
    fun LabeledSwitch.onToggleListener(switchToggle: SwitchToggle){
        this.setOnClickListener { switchToggle.toggleChange(!isOn) }
    }

    interface SwitchToggle{
        fun toggleChange(isOn: Boolean)
    }
}