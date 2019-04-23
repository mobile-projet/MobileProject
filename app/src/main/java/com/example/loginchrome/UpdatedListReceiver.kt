package com.example.loginchrome

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UpdatedListReceiver(val frag: ViewOrdersFragment?=null, val model: OrderViewModel? = null) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        when(intent.action) {
            UpdateService.UPDATE_LIST -> {
                model?.items?.postValue(intent.extras?.get("list") as List<OrderItem>? ?: listOf())
            }
        }
    }

}